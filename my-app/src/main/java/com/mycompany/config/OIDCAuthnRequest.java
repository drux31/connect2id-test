package com.mycompany.config;

import java.io.IOException;
import java.net.*;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.openid.connect.sdk.*;
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

    public OIDCAuthnRequest() throws URISyntaxException {
        this.loadPropertiesFIle = new LoadPropertiesFIle();
        System.out.println(loadPropertiesFIle.toString());
        clientID = new ClientID(loadPropertiesFIle.getCleintID());
        System.out.println(clientID);
        callback = new URI(loadPropertiesFIle.getCallBackURL());
        state = new State();
        nonce = new Nonce();
        oidcProviderURI = loadPropertiesFIle.getOidcProviderURL();
    }

    //public AuthorizationCode getAuthCode() throws URISyntaxException, MalformedURLException, IOException {
    public void getAuthCode() throws URISyntaxException, MalformedURLException, IOException {
        AuthorizationCode code = null;

        // generate the auhtentication request
        AuthenticationRequest request = new AuthenticationRequest.Builder(
            new ResponseType("code"),
            new Scope("openid"),
            clientID,
            callback
        ).endpointURI(new URI(oidcProviderURI)).state(state).nonce(nonce).responseMode(new ResponseMode("form_post")).build();

        // print the request uri
        URI requestURI = request.toURI();
        System.out.println(requestURI);
        
        
        /* Process the request */
        //Response response = request.getIDTokenHint();

        //return code ;
    }

    private void parseAuthResponse(URI requestUri) {

    }
}
