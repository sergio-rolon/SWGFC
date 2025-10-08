package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Asignaciones;
import mktpromomarc.flotilla.repository.AsignacionesRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.AsignacionesService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Asignaciones", urlPatterns = {"/asignaciones/*"})
public class AsignacionesController extends HttpServlet {
    private Gson gson = new Gson();
    AsignacionesRepository asignacionesRepository = new AsignacionesRepository();
    AsignacionesService asignacionesService = new AsignacionesService(asignacionesRepository);
    String clase = getClass().getSimpleName();
    Boolean isDoPut=false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        String pathInfo=request.getPathInfo() !=null ? request.getPathInfo():"";
        boolean isAsesor=role.equals("Asesor");
        String emailAsesor="";
        Util.logInfo("Se ejecutó DoGet", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(role.equals("Operación") && pathInfo.isEmpty() || isAsesor && pathInfo.isEmpty()) {
            emailAsesor=isAsesor?email:"";
            try (PrintWriter out = response.getWriter()) {

                JSONArray asignacionesResult = asignacionesService.getAll(isAsesor, emailAsesor);

                if (asignacionesResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(asignacionesResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All asignaciones recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay asignaciones registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for "+role+" role and sent in response", clase);
                out.flush();
                return;
            } catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }
        }
        if(role.equals("Operación") && !pathInfo.isEmpty()){
            try (PrintWriter out = response.getWriter()) {

                JSONArray asignacionesResult = asignacionesRepository.findAllObjects(pathInfo);

                if (asignacionesResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(asignacionesResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All asignaciones recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay asignaciones registradas\"}";
                out.print(errorResponse);
                Util.logInfo("None asignaciones recovered for "+role+" role and sent in response", clase);
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

        if(role.equals("Operación")) {
            try (PrintWriter out = response.getWriter()) {
                String contentType = request.getContentType();
                if (!("application/json".equals(contentType))) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Error en el servidor");
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
                    Asignaciones newAsignacion = new Asignaciones();
                    newAsignacion.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newAsignacion.setIdVehiculo(jsonObject.getInt("idVehiculo"));
                    newAsignacion.setIdEmpleado(jsonObject.getInt("idEmpleado"));
                    int idCliente = jsonObject.getInt("idCliente");
                    Asignaciones asignacionResult = asignacionesService.add(newAsignacion, idCliente);
                    if (asignacionResult != null) {
                        if(!(asignacionResult.getIdAsignacion()==-1) && !(asignacionResult.getIdAsignacion()==-2)){
                            Util.logInfo("Asignación registrada exitosamente", clase);
                            response.setStatus(HttpServletResponse.SC_OK);
                            String successResponse = "{\"success\": \"Asignación registrada exitosamente\"}";
                            out.print(successResponse);
                            out.flush();
                            return;
                        }
                        if(asignacionResult.getIdAsignacion()==-2) {
                            response.setStatus(HttpServletResponse.SC_CONFLICT);
                            String errorResponse = "{\"error\": \"Vehículo y/o empleado no pertenecen al mismo cliente.\"}";
                            out.print(errorResponse);
                            out.flush();
                            return;
                        }

                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        String errorResponse = "{\"error\": \"El id de asignación ya está asignado, intenta con otro\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"La asignacion ya existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                } catch (IOException ex) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
                }
            }catch (IOException ex) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
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

        if(role.equals("Operación")) {
            try (PrintWriter out = response.getWriter()) {
                String contentType = request.getContentType();
                if (!("application/json".equals(contentType))) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Error en el servidor");
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
                    Asignaciones newAsignacion = new Asignaciones();
                    newAsignacion.setIdAsignacion(jsonObject.getInt("idAsignacion"));
                    newAsignacion.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newAsignacion.setIdVehiculo(jsonObject.getInt("idVehiculo"));
                    newAsignacion.setIdEmpleado(jsonObject.getInt("idEmpleado"));
                    int idCliente = jsonObject.getInt("idCliente");
                    Asignaciones asignacionResult = asignacionesService.update(newAsignacion, idCliente);
                    isDoPut=false;
                    if (asignacionResult != null) {
                        if(asignacionResult.getIdAsignacion()==-2) {
                            response.setStatus(HttpServletResponse.SC_CONFLICT);
                            String errorResponse = "{\"error\": \"Vehículo y/o empleado no pertenecen al mismo cliente.\"}";
                            out.print(errorResponse);
                            out.flush();
                            return;
                        }
                        Util.logInfo("Asignacion modificada exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Asignacion modificada exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }

                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Id asignación ingresado no existe, intentar con otro\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }catch (IOException ex) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
                }
            }catch (IOException ex) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
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

        if(role.equals("Operación")) {
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

                    if (!Validator.isNum("Id asignación",jsonObject.getString("idAsignacion")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Id asignación inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Asignaciones asignacionToDelete = new Asignaciones();
                    asignacionToDelete.setIdAsignacion(jsonObject.getInt("idAsignacion"));

                    if (asignacionesService.delete(asignacionToDelete.getIdAsignacion())) {
                        Util.logInfo("Asignación eliminada correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Asignación eliminada exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Asignación no existe\"}";
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
            sb.append(Validator.isNum("Id asignación",String.valueOf(jsonObject.get("idAsignacion")))).append(",");
        }
        sb.append(Validator.isNumTwoTypes("Id tipo estatus",String.valueOf(jsonObject.get("idTipoEstatus")))).append(",");
        sb.append(Validator.isNum("Id empleado",String.valueOf(jsonObject.get("idEmpleado")))).append(",");
        sb.append(Validator.isNum("Id vehículo",String.valueOf(jsonObject.get("idVehiculo"))));
        sb.append(Validator.isNum("Id cliente",String.valueOf(jsonObject.get("idCliente"))));
        sb.append("}");

        return sb.toString();
    }
}



