package org.example.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/employee")
public class SimpleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        PrintWriter writer = response.getWriter();

        writer.println("<html><title>Welcome</title><body>");
        writer.println("<h1>Have a wonderful day!</h1>");
        writer.println("</body></html>");

    }
}
