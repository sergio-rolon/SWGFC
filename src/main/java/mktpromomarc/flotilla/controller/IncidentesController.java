package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Incidentes;
import mktpromomarc.flotilla.repository.IncidentesRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.IncidentesService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Incidentes", urlPatterns = {"/incidentes/*"})
public class IncidentesController extends HttpServlet {
    private Gson gson = new Gson();
    IncidentesRepository incidentesRepository = new IncidentesRepository();
    IncidentesService incidentesService = new IncidentesService(incidentesRepository);
    String clase = getClass().getSimpleName();
    Boolean isDoPut=false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = (String) request.getAttribute("email");

        String role = (String) request.getAttribute("role");
        Util.logInfo("Se ejecutó DoGet", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        boolean isAsesor=role.equals("Asesor");
        String emailAsesor="";
        if(role.equals("Operación") || role.equals("Administrador")  || isAsesor) {
            emailAsesor=isAsesor?email:"";
            try (PrintWriter out = response.getWriter()) {

                JSONArray incidentesResult = incidentesService.getAll(isAsesor, emailAsesor);

                if (incidentesResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(incidentesResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All incidentes recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay incidentes registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for "+role+" role and sent in response", clase);
                out.flush();
                return;
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
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        Util.logInfo("Se ejecutó DoPost", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(role.equals("Operación") || role.equals("Administrador") ) {
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
                    Incidentes newIncidente = new Incidentes();
                    newIncidente.setIdTipoIncidente(jsonObject.getInt("idTipoIncidente"));
                    newIncidente.setDescripcion(jsonObject.getString("descripcion"));
                    newIncidente.setFechaIncidente(jsonObject.getString("fechaIncidente"));
                    newIncidente.setIdAsignacion(jsonObject.getInt("idAsignacion"));

                    Incidentes incidenteResult = incidentesService.add(newIncidente);
                    if (incidenteResult != null) {
                        if(!(incidenteResult.getIdIncidente()==-1)){
                            Util.logInfo("Incidente registrado exitosamente", clase);
                            response.setStatus(HttpServletResponse.SC_OK);
                            String successResponse = "{\"success\": \"Incidente registrado exitosamente\"}";
                            out.print(successResponse);
                            out.flush();
                            return;
                        }
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        String errorResponse = "{\"error\": \"El id incidente ya está asignado, intenta con otro\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"El incidente ya existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                } catch (IOException ex) {
                    request.setAttribute("message", "There was an error: " + ex.getMessage());
                }
            }
        }
        Util.logInfo("Access denied for user "+email+" with role "+role+" ", clase);
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        Util.logInfo("Se ejecutó DoPut", clase);
        isDoPut=true;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(role.equals("Operación") || role.equals("Administrador") ) {
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
                    Incidentes newIncidente = new Incidentes();
                    newIncidente.setIdIncidente(jsonObject.getInt("idIncidente"));
                    newIncidente.setIdTipoIncidente(jsonObject.getInt("idTipoIncidente"));
                    newIncidente.setDescripcion(jsonObject.getString("descripcion"));
                    newIncidente.setFechaIncidente(jsonObject.getString("fechaIncidente"));
                    newIncidente.setIdAsignacion(jsonObject.getInt("idAsignacion"));

                    Incidentes incidenteResult = incidentesService.update(newIncidente);
                    isDoPut=false;
                    if (incidenteResult != null) {
                        Util.logInfo("Incidente modificado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Incidente modificado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Id incidente ingresado ya está asignado, intentar con otro\"}";
                    out.print(errorResponse);
                    out.flush();
                } catch (IOException ex) {
                    request.setAttribute("message", "There was an error: " + ex.getMessage());
                }
            }
        }
        Util.logInfo("Access denied for user "+email+" with role "+role+" ", clase);
        response.sendRedirect("/index.html");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        Util.logInfo("Se ejecutó DoDelete", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(role.equals("Operación") || role.equals("Administrador") ) {
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

                    if (!Validator.isNum("id incidente", jsonObject.getString("idIncidente")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Id de incidente inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Incidentes incidenteToDelete = new Incidentes();
                    incidenteToDelete.setIdIncidente(jsonObject.getInt("idIncidente"));

                    if (incidentesService.delete(incidenteToDelete.getIdIncidente())) {
                        Util.logInfo("Incidente eliminado correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Incidente eliminado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Incidente no existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }
            }
        }
        Util.logInfo("Access denied for user "+email+" with role "+role+" ", clase);
        response.sendRedirect("/index.html");
    }

    private String validateFields(JSONObject jsonObject){
        Validator.validationFailed=false;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if(isDoPut) {
            sb.append(Validator.isNum("Id incidente", jsonObject.getString("idIncidente"))).append(",");
        }
        sb.append(Validator.isNumTwoTypes("Id tipo incidente", jsonObject.getString("idTipoIncidente"))).append(",");
        sb.append(Validator.isString("Descripción", jsonObject.getString("descripcion"))).append(",");
        sb.append(Validator.isDate("Fecha de incidente", jsonObject.getString("fechaIncidente"))).append(",");
        sb.append(Validator.isNum("Id asignación",String.valueOf(jsonObject.get("idAsignacion"))));
        sb.append("}");

        return sb.toString();
    }
}



