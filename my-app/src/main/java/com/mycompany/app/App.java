package com.mycompany.app;

import java.net.URISyntaxException;
import com.mycompany.config.OIDCAuthnRequest;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        
        try {
            new OIDCAuthnRequest().getAuthCode();
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }

        new HelloOIDC().runServer();
        
    }
}
