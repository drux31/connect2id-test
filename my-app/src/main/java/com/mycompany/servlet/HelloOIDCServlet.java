package com.mycompany.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HelloOIDCServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello Servlet</h1>");
        response.getWriter().println("session=" + request.getSession(true).getId());
        response.setContentType("text/html");
        
        PrintWriter writer = response.getWriter();
        writer.append(" <form action=\"login\" method=\"POST\">\r\n")
        .append(" <input type=\"submit\" value=\"Clieck here to login\"/>\r\n")
        .append(" </form>\r\n");
        /*
         *  <form method="post" action="login">
        </form>
        <button type="submit" form="form1" value="Submit">Click here to login</button>
         */
    }
}
