package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.modelo.Clientes;
import mktpromomarc.flotilla.repository.ClientesRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.ClientesService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Clientes", urlPatterns = {"/clientes/*"})
public class ClientesController extends HttpServlet {
    private Gson gson = new Gson();
    ClientesRepository clientesRepository = new ClientesRepository();
    ClientesService clientesService = new ClientesService(clientesRepository);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (PrintWriter out = response.getWriter()) {

            JSONArray clientesResult = clientesService.getAll();

            if (clientesResult != null) {
                System.out.println("Clientes recuperados");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String successResponse = new Gson().toJson(clientesResult);
                out.print(successResponse);
                out.flush();
                return;
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String errorResponse = "{\"error\": \"No hay clientes registrados\"}";
            out.print(errorResponse);
            out.flush();

        } catch (IOException ex) {
            request.setAttribute("message", "There was an error: " + ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Se ejecuto doPost");
        try (PrintWriter out = response.getWriter()) {
            String contentType = request.getContentType();
            if (!("application/json".equals(contentType))) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid"
                        + "content type");
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                JSONObject jsonObject = new JSONObject(json);

                String resultValidation = clientesValidator(jsonObject);
                System.out.println("Resultado validación:"+ resultValidation);
                if(Validator.validationFailed){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(resultValidation);
                    out.flush();
                    return;
                }
                Clientes newUser = new Clientes();
                newUser.setRazonSocial(jsonObject.getString("razonSocial"));
                newUser.setRfc(jsonObject.getString("rfc").toUpperCase());
                newUser.setIdEstatus(jsonObject.getInt("idEstatus"));
                newUser.setIdUsuario(jsonObject.getInt("idUsuario"));

                Clientes clienteResult = clientesService.add(newUser);
                if (clienteResult != null) {
                    System.out.println("Cliente agregado correctamente");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String successResponse = "{\"success\": \"Cliente registrado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String errorResponse = "{\"error\": \"Cliente ya existe\"}";
                out.print(errorResponse);
                out.flush();
            } catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Se ejecuto doPut");
        try (PrintWriter out = response.getWriter()) {
            String contentType = request.getContentType();
            if (!("application/json".equals(contentType))) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid"
                        + "content type");
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                JSONObject jsonObject = new JSONObject(json);

                String resultValidation = clientesValidator(jsonObject);
                System.out.println("Resultado validación:"+ resultValidation);
                if(Validator.validationFailed){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(resultValidation);
                    out.flush();
                    return;
                }
                Clientes newUser = new Clientes();
                newUser.setRazonSocial(jsonObject.getString("razonSocial"));
                newUser.setRfc(jsonObject.getString("rfc").toUpperCase());
                newUser.setIdEstatus(jsonObject.getInt("idEstatus"));
                newUser.setIdUsuario(jsonObject.getInt("idUsuario"));

                Clientes clienteResult = clientesService.update(newUser);
                if (clienteResult != null) {
                    System.out.println("Cliente agregado correctamente");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String successResponse = "{\"success\": \"Cliente modificado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String errorResponse = "{\"error\": \"Cliente no existe\"}";
                out.print(errorResponse);
                out.flush();
            } catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (PrintWriter out = response.getWriter()) {
            String contentType = request.getContentType();
            if (!("application/json".equals(contentType))) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid"
                        + "content type");
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                JSONObject jsonObject = new JSONObject(json);

                // TODO ESTO  NO SE PORQUE LO PUSE
                if (!Validator.isRfc(jsonObject.getString("rfc")).contains("success")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String errorResponse = "{\"error\": \"RFC inválido\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }

                Clientes deleteUser = new Clientes();
                deleteUser.setRfc(jsonObject.getString("rfc").toUpperCase());


                if (clientesService.delete(deleteUser.getRfc())) {
                    System.out.println("Cliente eliminado correctamente");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String successResponse = "{\"success\": \"Cliente eliminado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String errorResponse = "{\"error\": \"Cliente no existe\"}";
                out.print(errorResponse);
                out.flush();
            }
        }
    }

    private String clientesValidator(JSONObject jsonObject){
        Validator.validationFailed=false;
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append(Validator.isAlphaNum("Razón social",jsonObject.getString("razonSocial"))).append(",");
        sb.append(Validator.isRfc(jsonObject.getString("rfc"))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Estatus",String.valueOf(jsonObject.get("idEstatus")))).append(",");
        sb.append(Validator.isNum("Id Usuario",String.valueOf(jsonObject.get("idUsuario"))));
        sb.append("}");

        return sb.toString();
    }
}



