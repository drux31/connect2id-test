package com.mycompany.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Scanner;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.openid.connect.sdk.*;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.oauth2.sdk.id.*;

/**
 * Class handling the process for requesting an authentication ID from OIDC provider
 */
public class OIDCAuthnRequest {

    private LoadPropertiesFIle loadPropertiesFIle;
    private ClientID clientID ;
    private URI callback ;
    private Nonce nonce;
    private State state;
    private String oidcProviderURI ;


    public OIDCAuthnRequest() {
        this.loadPropertiesFIle = new LoadPropertiesFIle();
        System.out.println(loadPropertiesFIle.toString());
        clientID = new ClientID(loadPropertiesFIle.getCleintID());
        System.out.println(clientID);
        state = new State();
        nonce = new Nonce();
        oidcProviderURI = loadPropertiesFIle.getOidcProviderURL();
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
            ).endpointURI(getProviderMetadata().getAuthorizationEndpointURI()).state(state).nonce(nonce).responseMode(new ResponseMode("form_post")).build();

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
}
