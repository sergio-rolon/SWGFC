package org.example.controller;



import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.security.JwtGenerator;
import org.example.modelo.Person;
import org.example.dto.Token;


@WebServlet(urlPatterns = "/ServletPerson")
public class ServletPerson extends HttpServlet {
    private Gson gson = new Gson();
    /* protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet JsonPerson</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet JsonPerson at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }*/


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

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String contentType = request.getContentType();
        if(!("application/json".equals(contentType))){
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid"
                    + "content type");
            return;
        }

        try(BufferedReader reader = request.getReader()){
            Person newUser = gson.fromJson(reader, Person.class);
            Token tokenGenerated = JwtGenerator.generateToken(newUser.getPassword(), newUser.getRole());
            String newToken = new Gson().toJson(tokenGenerated.getAccessToken());
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(newToken);
            out.flush();
            //response.getWriter().append("Added new Person with name: ").append(responsePerson.getFirsName());

        } catch (IOException ex){
            request.setAttribute("message", "There was an error: "+ex.getMessage());
        }
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

