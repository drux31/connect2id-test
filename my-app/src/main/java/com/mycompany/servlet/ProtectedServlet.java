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
        
        //String authCode = (String)request.getAttribute("authCode");
        String name = (String) request.getSession().getAttribute("userName"); 
        String email = (String) request.getSession().getAttribute("userEmail");

        out.append("<!DOCTYPE html>\r\n")
            .append("<html>\r\n")
            .append(" <head>\r\n")
            .append(" <title>Protected resources</title>\r\n")
            .append(" </head>\r\n")
            .append(" <body>\r\n")
            .append(" <form action=\"logout\" method=\"GET\">\r\n")
            .append(" <input type=\"submit\" value=\"logout\"/>\r\n")
            .append(" </form>\r\n")
            .append(" User name: ").append(name).append("!<br/>\r\n")
            .append(" User email: ").append(email).append("!<br/>\r\n")
            .append(" </body>\r\n")
            .append("</html>\r\n");
    } 
}
