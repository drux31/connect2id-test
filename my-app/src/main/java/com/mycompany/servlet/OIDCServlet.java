package com.mycompany.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Scanner;

import com.mycompany.config.OIDCAuthnRequest;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
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
        /*
        AuthenticationResponse authResponse = null;
        AuthorizationCode authCode = null ;
        try {
            System.out.println(request.toString());
            authResponse = AuthenticationResponseParser.parse(new URI(request.getRequestURI()));
            System.out.println(request.getRequestURI());

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
        */
        //System.out.println(request.getMethod() + " " + request.getHeader("Host") + request.getRequestURI() + "\n" + request.toString() + "\n");
        //System.out.println(request.getRequestURL());
        String body = extractPostRequestBody(request);
        //System.out.println("Body request: " + body);
        //request.setAttribute("authCode", body);
        request.getSession().setAttribute("authCode", body);        
        //RequestDispatcher rs = request.getRequestDispatcher(request.getContextPath() + "/welcome");
        //rs.forward(request, response);
        response.sendRedirect(request.getContextPath() + "/protected/welcome");
        // new ProtectedServlet().doGet(request, response);
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
