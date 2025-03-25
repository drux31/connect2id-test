package com.mycompany.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.oauth2.sdk.ParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CallBackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // Create an authentication response from the respone callback
        AuthenticationResponse authResponse = null;
        try {
            authResponse = AuthenticationResponseParser.parse(new URI(request.getRequestURI()));

        } catch (ParseException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Make sure the request is correct
        

    }
}
