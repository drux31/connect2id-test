package com.mycompany.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.mycompany.config.OIDCAuthnRequest;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;

import jakarta.servlet.RequestDispatcher;
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // redirect to OIDC provider for authorisation
        
        response.sendRedirect(oidcAuthnRequest.getEndPointURI());
    }  

     @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Create an authentication response from the respone callback
        AuthenticationResponse authResponse = null;
        AuthorizationCode authCode = null ;
        try {
            authResponse = AuthenticationResponseParser.parse(new URI(request.getRequestURI()));

        } catch (ParseException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Make sure the authentication response is valid
        if (oidcAuthnRequest.isValid(authResponse)) {
            authCode = authResponse.toSuccessResponse().getAuthorizationCode();
        }

        // request access token
        // print the authCode
        Object data = authCode.toString();
        request.setAttribute("authCode", data);
        RequestDispatcher rs = request.getRequestDispatcher("welcome");
        rs.forward(request, response);

    }

}
