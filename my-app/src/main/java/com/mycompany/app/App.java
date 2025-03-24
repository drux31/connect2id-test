package com.mycompany.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import com.mycompany.config.OIDCAuthnRequest;
import com.nimbusds.oauth2.sdk.ParseException;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            new OIDCAuthnRequest().getAuthCode();
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        
    }
}
