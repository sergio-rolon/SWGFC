package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Servicios;
import mktpromomarc.flotilla.repository.ServiciosRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.ServiciosService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Servicios", urlPatterns = {"/servicios/*"})
public class ServiciosController extends HttpServlet {
    private Gson gson = new Gson();
    ServiciosRepository serviciosRepository = new ServiciosRepository();
    ServiciosService serviciosService = new ServiciosService(serviciosRepository);
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
        if(role.equals("Operación") || role.equals("Administrador") || isAsesor) {
            emailAsesor=isAsesor?email:"";
            try (PrintWriter out = response.getWriter()) {

                JSONArray serviciosResult = serviciosService.getAll(isAsesor, emailAsesor);

                if (serviciosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(serviciosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All servicios recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay servicios registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for "+role+" role and sent in response", clase);
                out.flush();
                return;
            } catch (IOException ex) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
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
                    Servicios newServicio = new Servicios(jsonObject.getBigDecimal("costo")
                            ,jsonObject.getBigDecimal("comision"));
                    newServicio.setIdTipoServicio(jsonObject.getInt("idTipoServicio"));
                    newServicio.setKilometraje(jsonObject.getInt("kilometraje"));
                    newServicio.setFechaServicio(jsonObject.getString("fechaServicio"));
                    newServicio.setIdAsignacion(jsonObject.getInt("idAsignacion"));

                    Servicios servicioResult = serviciosService.add(newServicio);
                    if (servicioResult != null) {
                        if(!(servicioResult.getIdServicio()==-1)){
                            Util.logInfo("Servicio registrado exitosamente", clase);
                            response.setStatus(HttpServletResponse.SC_OK);
                            String successResponse = "{\"success\": \"Servicio registrado exitosamente\"}";
                            out.print(successResponse);
                            out.flush();
                            return;
                        }
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                        String errorResponse = "{\"error\": \"El número de serie ya está asignado, intenta con otro\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"El servicio ya existe\"}";
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

        if(role.equals("Operación") || role.equals("Administrador") ) {
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
                    Servicios newServicio = new Servicios(jsonObject.getBigDecimal("costo")
                            ,jsonObject.getBigDecimal("comision"));
                    newServicio.setIdServicio(jsonObject.getInt("idServicio"));
                    newServicio.setIdTipoServicio(jsonObject.getInt("idTipoServicio"));
                    newServicio.setKilometraje(jsonObject.getInt("kilometraje"));
                    newServicio.setFechaServicio(jsonObject.getString("fechaServicio"));
                    newServicio.setIdAsignacion(jsonObject.getInt("idAsignacion"));

                    Servicios servicioResult = serviciosService.update(newServicio);
                    isDoPut=false;
                    if (servicioResult != null) {
                        Util.logInfo("Servicio modificado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Servicio modificado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Id de servicio ingresado ya está asignado, intentar con otro\"}";
                    out.print(errorResponse);
                    out.flush();
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

        if(role.equals("Operación") || role.equals("Administrador") ) {
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

                    if (!Validator.isNum("id servicio", jsonObject.getString("idServicio")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Id de servicio inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Servicios servicioToDelete = new Servicios();
                    servicioToDelete.setIdServicio(jsonObject.getInt("idServicio"));

                    if (serviciosService.delete(servicioToDelete.getIdServicio())) {
                        Util.logInfo("Servicio eliminado correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Servicio eliminado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Servicio no existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }
            }catch (IOException ex) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
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
            sb.append(Validator.isNum("Id servicio", jsonObject.getString("idServicio"))).append(",");
        }
        sb.append(Validator.isNumThreeTypes("Id tipo servicio", jsonObject.getString("idTipoServicio"))).append(",");
        sb.append(Validator.isNum("Kilometraje", jsonObject.getString("kilometraje"))).append(",");
        sb.append(Validator.isDate("Fecha de servicio", jsonObject.getString("fechaServicio"))).append(",");
        sb.append(Validator.isBigDecimal("Costo",jsonObject.getString("costo"))).append(",");
        sb.append(Validator.isBigDecimal("Comisión",jsonObject.getString("comision"))).append(",");
        sb.append(Validator.isNum("Id asignación",String.valueOf(jsonObject.get("idAsignacion"))));
        sb.append("}");

        return sb.toString();
    }
}



