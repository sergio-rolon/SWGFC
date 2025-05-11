package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
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
    String clase = getClass().getSimpleName();
    Boolean isDoPut=false;
    public static Boolean isAsesor=false;
    public static String emailAsesor="";
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        String requestUrl = request.getRequestURI();
        Util.logInfo("Se ejecutó DoGet", clase);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        isAsesor=role.equals("asesor");
        if((role.equals("operacion") || isAsesor) && requestUrl.equals("/api/clientes/getAllClientes")){
            emailAsesor=isAsesor?email:"";
            try (PrintWriter out = response.getWriter()) {

                JSONArray clientesResult = clientesRepository.findAllObjects();

                if (clientesResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(clientesResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All clients recovered for operacion role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay clientes registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for operacion role and sent in response", clase);
                out.flush();

            } catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }//try

        }

        if(role.equals("operacion")) {
            try (PrintWriter out = response.getWriter()) {

                JSONArray clientesResult = clientesService.getAll();

                if (clientesResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(clientesResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All clientes recovered for operacion role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay clientes registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for operation role and sent in response", clase);
                out.flush();

            } catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }
        }
        Util.logInfo("Access denied for user "+email+" with role "+role+" ", clase);
        response.sendRedirect("/index.html");
    }//doGet

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Util.logInfo("Se ejecutó DoPost", clase);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
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

                String resultValidation = validateFields(jsonObject);
                Util.logInfo("Resultado validación:"+ resultValidation, clase);
                if(Validator.validationFailed){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(resultValidation);
                    out.flush();
                    return;
                }
                Clientes newCliente = new Clientes();
                newCliente.setRazonSocial(jsonObject.getString("razonSocial"));
                newCliente.setRfc(jsonObject.getString("rfc").toUpperCase());
                newCliente.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                newCliente.setIdUsuario(jsonObject.getInt("idUsuario"));

                Clientes clienteResult = clientesService.add(newCliente);
                if (clienteResult != null) {
                    Util.logInfo("Cliente registrado exitosamente", clase);
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = "{\"success\": \"Cliente registrado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                String errorResponse = "{\"error\": \"El cliente ya existe\"}";
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
        Util.logInfo("Se ejecutó DoPut", clase);
        isDoPut=true;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String contentType = request.getContentType();
            if (!("application/json".equals(contentType))) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid"
                        + "content type");
                isDoPut=false;
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

                String resultValidation = validateFields(jsonObject);
                Util.logInfo("Resultado validación:"+ resultValidation, clase);
                if(Validator.validationFailed){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(resultValidation);
                    out.flush();
                    isDoPut=false;
                    return;
                }
                Clientes newCliente = new Clientes();
                newCliente.setIdCliente(jsonObject.getInt("idCliente"));
                newCliente.setRazonSocial(jsonObject.getString("razonSocial"));
                newCliente.setRfc(jsonObject.getString("rfc").toUpperCase());
                newCliente.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                newCliente.setIdUsuario(jsonObject.getInt("idUsuario"));

                Clientes clienteResult = clientesService.update(newCliente);
                isDoPut=false;
                if (clienteResult != null) {
                    Util.logInfo("Cliente modificado exitosamente", clase);
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = "{\"success\": \"Cliente modificado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                String errorResponse = "{\"error\": \"RFC ingresado ya está asignado, intentar con otro\"}";
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
        Util.logInfo("Se ejecutó DoDelete", clase);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
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

                if (!Validator.isRfc(jsonObject.getString("rfc")).contains("success")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    String errorResponse = "{\"error\": \"RFC inválido\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }

                Clientes clienteToDelete = new Clientes();
                clienteToDelete.setRfc(jsonObject.getString("rfc").toUpperCase());

                if (clientesService.delete(clienteToDelete.getRfc())) {
                    Util.logInfo("Cliente eliminado correctamente", clase);
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = "{\"success\": \"Cliente eliminado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                String errorResponse = "{\"error\": \"Cliente no existe\"}";
                out.print(errorResponse);
                out.flush();
            }
        }
    }

    private String validateFields(JSONObject jsonObject){
        Validator.validationFailed=false;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if(isDoPut) {
            sb.append(Validator.isNum("Id Cliente", jsonObject.getString("idCliente"))).append(",");
        }
        sb.append(Validator.isAlphaNum("Razon social",jsonObject.getString("razonSocial"))).append(",");
        sb.append(Validator.isRfc(jsonObject.getString("rfc"))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Tipo Estatus",String.valueOf(jsonObject.get("idTipoEstatus")))).append(",");
        sb.append(Validator.isNum("Id Usuario",String.valueOf(jsonObject.get("idUsuario"))));
        sb.append("}");

        return sb.toString();
    }
}



