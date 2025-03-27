package com.mycompany.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Scanner;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;

import com.nimbusds.oauth2.sdk.id.*;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;

/**
 * Class handling the process for requesting an authentication ID from OIDC provider
 */
public class OIDCAuthnRequest {

    private LoadPropertiesFIle loadPropertiesFIle;
    private ClientID clientID ;
    private URI callback ;
    private Secret clientSecret;
    private Nonce nonce;
    private State state;
    private String oidcProviderURI ;
    private IDTokenClaimsSet idTokenClaimsSet;
    OIDCProviderMetadata oidcProviderMetadata ;

    public OIDCAuthnRequest() {
        this.loadPropertiesFIle = new LoadPropertiesFIle();
        System.out.println(loadPropertiesFIle.toString());
        clientID = new ClientID(loadPropertiesFIle.getClientID());
        System.out.println(clientID);
        clientSecret = new Secret(loadPropertiesFIle.getClientSecret());
        state = new State();
        nonce = new Nonce();
        oidcProviderURI = loadPropertiesFIle.getOidcProviderURL();
        // providerMetadata
        oidcProviderMetadata = getProviderMetadata();
    }

    /**
     * Get the OIDC provider Authorisation endpoint URI
     * @return URI 
     */
    public String getEndPointURI() {

        AuthenticationRequest request = null;
        URI requestURI = null;
        try {
            // generate the auhtentication request
            callback = new URI(loadPropertiesFIle.getCallBackURL());
            request = new AuthenticationRequest.Builder(
                new ResponseType("code"),
                new Scope("openid"),
                clientID,
                callback
            ).endpointURI(oidcProviderMetadata.getAuthorizationEndpointURI()).state(state).nonce(nonce).responseMode(new ResponseMode("form_post")).build();

            // print the request uri
            requestURI = request.toURI();
            System.out.println("Authz URI: " + requestURI);
        
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        /* Process the request */
        //Response response = request.getIDTokenHint();

        return requestURI.toString();
    }

    /**
     * Check that the response is valid 
     * @param response
     * @return true or false whether is a valid response or not
     */
    public boolean isValid(AuthenticationResponse response) {
        
        //check the state - return false if different from the initial one
        if (! response.getState().equals(state)){
            System.err.println("Unexpected authentication response");
            return false;
        }

        // check te instance - return false if diferent from AuthenticationResponse
        if (response instanceof AuthenticationErrorResponse) {
            System.err.println(response.toErrorResponse().getErrorObject());
            return false;
        }

        // if valid response, return true
        return true;
    }

    /**
     * Get the provider info from wel-known configuration endpoint
     * @return - a string with the provider info configuration
     */
    private OIDCProviderMetadata getProviderMetadata() {

        InputStream stream = null ;
        URI issuerURI = null;
        URL providerConfigurationURL = null;
        OIDCProviderMetadata providerMetadata = null;

        try {
            // Get the issuer URI witch the default OIDC provider URI
            issuerURI = new URI("https://" + oidcProviderURI);
            System.out.println("OIDC provider URI: " + issuerURI);
            // resolve the well-known config endpoint
            providerConfigurationURL = issuerURI.resolve("/.well-known/openid-configuration").toURL();
            // get the data
            stream = providerConfigurationURL.openStream();

            String providerInfo = null ;
            try (Scanner s = new Scanner(stream)) {
                providerInfo = s.useDelimiter("\\A").hasNext() ? s.next() : "";
            }
            // parse into metadata
            providerMetadata = OIDCProviderMetadata.parse(providerInfo);
            //System.out.println("provider info: " + providerInfo);
        }
        catch (URISyntaxException | MalformedURLException e ) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }  
        return providerMetadata;
    }

    /**
     *  construct the token request usign the code grant
     *  @return - OIDCTokenResponse instqnce containing the access token, the id token and the refresh token  
     */
    public OIDCTokenResponse requestToken(String callback, AuthorizationCode code) {

        //Construct the code grant
        AuthorizationGrant codeGrant = null; 
        OIDCTokenResponse successResponse = null;

        try {
            codeGrant = new AuthorizationCodeGrant(code, new URI(callback)); 

            // credentials for authenticating the client
            ClientAuthentication cleintAuth = new ClientSecretBasic(clientID, clientSecret);

            // token endpoint
            URI tokendEndpoint = oidcProviderMetadata.getTokenEndpointURI();

            // Make the token request
            TokenRequest tokenRequest = new TokenRequest(tokendEndpoint, cleintAuth, codeGrant, new Scope("openid"));
            TokenResponse tokenResponse = OIDCTokenResponseParser.parse(tokenRequest.toHTTPRequest().send());
            successResponse = (OIDCTokenResponse)tokenResponse.toSuccessResponse();

        }catch(URISyntaxException | ParseException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        
        return successResponse;
    }


    /**
     * The token validator checks the following:
     * Checks if the ID token JWS algorithm matches the expected one.
     * Checks the ID token signature (or HMAC) using the provided key material (from the JWK set URL or the client secret).
     * Checks if the ID token issuer (iss) and audience (aud) match the expected IdP and client_id.
     * Checks if the ID token is within the specified validity window (between the given issue time and expiration time, given a 1 minute leeway to accommodate clock skew).
     * Check the nonce value if one is expected.
     * 
     * @param idToken - the idToken to be validated
     * @return true or false whether the token is valid
     */
    public boolean isValidToken(JWT idToken) {
        
        // set the required parameters
        URL jwkSetURL = null ;
        try {
            jwkSetURL = oidcProviderMetadata.getJWKSetURI().toURL();
            JWSAlgorithm jwsAlgo = JWSAlgorithm.RS256;

            // ceate the validator
            IDTokenValidator tokenValidator = new IDTokenValidator(oidcProviderMetadata.getIssuer(), clientID, jwsAlgo, jwkSetURL);

            // get the claims
            idTokenClaimsSet = tokenValidator.validate(idToken, nonce);
            
        } catch(MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch(BadJOSEException e){
            //Invalid signature or claims
            System.err.println("Invalid signature or claims: " + e.getMessage());
        } catch (JOSEException e) {
            // Internal processing exception
            System.err.println("Internal processing exception: " + e.getMessage());
        } finally {
            if (idTokenClaimsSet == null) {
                return false;
            }
        }

        // the token is valid
        return true ;
    }

    /**
     * get the Logged in subject
     * @return
     */
    public String getLoggedInUser() {
        return idTokenClaimsSet.getSubject().toJSONString();
    }

    /**
     * Using the access token to make user info request.
     * @param accessToken - access token obtained from the earlier processes
     * @return JSON onject containing the user information
     */
    public UserInfo getUserInfoClaims(AccessToken accessToken) {

        UserInfoRequest userInfoReq = new UserInfoRequest(oidcProviderMetadata.getUserInfoEndpointURI(), (BearerAccessToken) accessToken);

        // fetch the response
        HTTPResponse userInfoHTTPResp = null;
        try {
            userInfoHTTPResp = userInfoReq.toHTTPRequest().send();
        } catch(SerializeException | IOException e) {
            System.err.println(e.getMessage());
        }

        // Parse the user info
        UserInfoResponse userInfoResponse = null;
        try {
            userInfoResponse = UserInfoResponse.parse(userInfoHTTPResp);
        } catch(ParseException e) {
            System.err.println(e.getMessage());
        }
        // Hqndle the error if there is one
        if (userInfoResponse instanceof UserInfoErrorResponse) {
            ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
            System.err.println(error);
            return null;
        }

        UserInfoSuccessResponse userInfoSuccessResponse = (UserInfoSuccessResponse) userInfoResponse;
        return userInfoSuccessResponse.getUserInfo();
    }


}
