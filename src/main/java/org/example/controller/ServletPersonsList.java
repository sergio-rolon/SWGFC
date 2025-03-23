package org.example.controller;



import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.modelo.Person;
import org.example.repository.Service;


@WebServlet(urlPatterns = "/ServletPersonsList")
public class ServletPersonsList extends HttpServlet {
    private Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Person> persons = Service.getPersons();
        System.out.println(persons);
        String employeesJsonString = new Gson().toJson(persons);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(employeesJsonString);
        out.flush();

    }


    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

