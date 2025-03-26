package com.mycompany.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ProtectedServlet extends HttpServlet{
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
                
        PrintWriter out = response.getWriter();
        
        String authCode = request.getParameter("authCode");
        out.append("<!DOCTYPE html>\r\n")
            .append("<html>\r\n")
            .append(" <head>\r\n")
            .append(" <title>Protected resources</title>\r\n")
            .append(" </head>\r\n")
            .append(" <body>\r\n")
            .append(" Authorisation Code, ").append(authCode).append("!<br/><br/>\r\n")
            .append(" <form action=\"greeting\" method=\"POST\">\r\n")
            .append(" </body>\r\n")
            .append("</html>\r\n");
    } 
}
