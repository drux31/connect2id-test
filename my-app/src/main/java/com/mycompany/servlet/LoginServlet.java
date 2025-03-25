package com.mycompany.servlet;

import java.io.IOException;

import com.mycompany.config.OIDCAuthnRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // redirect to OIDC provider for authorisation
        
        response.sendRedirect(new OIDCAuthnRequest().getEndPointURI());
    }   

}
