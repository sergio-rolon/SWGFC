package org.example.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */


import com.google.gson.Gson;
import org.example.dto.Credentials;
import org.example.modelo.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.Token;
import org.example.security.JwtGenerator;
import org.example.security.RecaptchaVerifier;

/**
 *
 * @author Sergio Rolon
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String employeeJsonString = new Gson().toJson(new Person("Sergio", "Rolon", "Casa", "Admin", "Prueba12"));

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(employeeJsonString);
        out.flush();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Person registeredPerson = new Person("Sergio", "Rolon", "Casa", "Admin", "Prueba12");
        //response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String contentType = request.getContentType();

            if(!("application/json".equals(contentType))){
                response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid"
                        + "content type");
                return;
            }
            try(BufferedReader reader = request.getReader()){
                Credentials loginUser = gson.fromJson(reader, Credentials.class);
            if(RecaptchaVerifier.verifyRecaptcha(loginUser.getToken())) {
               if (loginUser.getPassword().equals(registeredPerson.getPassword())) {
                    System.out.println("Usuario logeado correctamente");

                   Token tokenGenerated = JwtGenerator.generateToken(loginUser.getPassword(), registeredPerson.getRole());
                   String tokenResponseString = new Gson().toJson(tokenGenerated);

                   System.out.println(tokenResponseString);

                   response.setContentType("application/json");
                   response.setCharacterEncoding("UTF-8");
                   out.print(tokenResponseString);
                   out.flush();


                } else {
                   String tokenResponseString = new Gson().toJson(new Token("false","true"));

                   response.setContentType("application/json");
                   response.setCharacterEncoding("UTF-8");
                   out.print(tokenResponseString);
                   out.flush();

                    System.out.println("Usuario o contraseña incorrecto");
                }//ifPassword
            }//ifRecaptcha
                else{
            String tokenResponseString = new Gson().toJson(new Token("false","false"));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(tokenResponseString);
            out.flush();

            }
            }

        } catch (IOException ex){
            request.setAttribute("message", "There was an error: "+ex.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
