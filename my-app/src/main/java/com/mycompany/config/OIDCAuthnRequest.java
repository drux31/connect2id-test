package com.mycompany.config;

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
        clientID = new ClientID(loadPropertiesFIle.getCleintID());
        callback = new URI(loadPropertiesFIle.getCallBackURL());
        state = new State();
        nonce = new Nonce();
        oidcProviderURI = loadPropertiesFIle.getOidcProviderURL();
    }

    public AuthorizationCode getAuthCode() throws URISyntaxException {
        AuthorizationCode code = null;

        // generate the auhtentication request
        AuthenticationRequest request = new AuthenticationRequest.Builder(
            new ResponseType("code"),
            new Scope("openid"),
            clientID,
            callback
        ).endpointURI(new URI(oidcProviderURI)).state(state).nonce(nonce).build();

        // print the request uri
        System.out.println(request.toURI());
        return code ;
    }

}
