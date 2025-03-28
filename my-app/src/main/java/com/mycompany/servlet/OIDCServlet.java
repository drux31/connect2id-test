package com.mycompany.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Scanner;

import com.mycompany.config.OIDCAuthnRequest;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class OIDCServlet extends HttpServlet {

    private final OIDCAuthnRequest oidcAuthnRequest = new OIDCAuthnRequest();

    /**
     *  Redirect the user to the OIDC provider
     *  Authentication page (if not already authenticated)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // redirect to OIDC provider for authorisation
        System.out.println(oidcAuthnRequest.getEndPointURI());
        //response.setHeader("Location", oidcAuthnRequest.getEndPointURI());
        response.setStatus(302);
        response.sendRedirect(oidcAuthnRequest.getEndPointURI());
    }  

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Create an authentication response from the respone callback
        AuthenticationResponse authResponse = null;
        AuthorizationCode authCode = null ;
        OIDCTokenResponse tokenResponse = null;
        try {
            System.out.println(request.toString());
            String reqURL = request.getRequestURL().toString() + "?" + extractPostRequestBody(request);
            System.out.println("callback: " + reqURL);
            authResponse = AuthenticationResponseParser.parse(new URI(reqURL));

        } catch (ParseException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Make sure the authentication response is valid
        // and get the auth code
        if (oidcAuthnRequest.isValid(authResponse)) {
            authCode = authResponse.toSuccessResponse().getAuthorizationCode();
        }
        String subject = null;
        UserInfo userInfo = null;
        // Get the token response using the code
        if (authCode != null) {
            tokenResponse = oidcAuthnRequest.requestToken(request.getRequestURL().toString(), authCode);

            //get the ID and access token, the server may also return a refresh token
            JWT idToken = tokenResponse.getOIDCTokens().getIDToken();
            try {
            System.out.println(idToken.getJWTClaimsSet().toJSONObject().toString());
            } catch(java.text.ParseException e) {
                System.err.println(e.getMessage());
            }

            AccessToken accessToken = tokenResponse.getOIDCTokens().getAccessToken();
            @SuppressWarnings("unused")
            RefreshToken refreshToken = tokenResponse.getOIDCTokens().getRefreshToken();

            if (oidcAuthnRequest.isValidToken(idToken)) {
                subject = oidcAuthnRequest.getLoggedInUser();
                userInfo = oidcAuthnRequest.getUserInfoClaims(accessToken);
            }
        }
        
        // Process the user information
        subject = userInfo.getSubject().toString();
        String email = userInfo.getEmailAddress();
        String name = userInfo.getName();

        // Set session information
        request.getSession().setAttribute("userSubject", subject);
        request.getSession().setAttribute("userEmail", email);
        request.getSession().setAttribute("userName", name);

        // redirect to the protected resource
        response.sendRedirect(request.getContextPath() + "/protected/welcome");
    }

    static String extractPostRequestBody(HttpServletRequest request) {
        String res = null;
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            
            System.out.println("Print headers");
            Enumeration<String> headerNames = request.getHeaderNames();
            while(headerNames.hasMoreElements()) {
                String headerName = (String)headerNames.nextElement();
                System.out.println(headerName + ": " + request.getHeader(headerName));
            }
            // Get the body content
            try (Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A")) {
                res = s.hasNext() ? s.next() : "";
            } catch (IOException e) {
                e.printStackTrace();
            }
           System.out.println(res);
        }
        return res;
    }

}
