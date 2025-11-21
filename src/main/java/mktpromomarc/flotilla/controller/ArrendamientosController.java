package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Arrendamientos;
import mktpromomarc.flotilla.repository.ArrendamientosRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.ArrendamientosService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Arrendamientos", urlPatterns = {"/arrendamientos/*"})
public class ArrendamientosController extends HttpServlet {
    private Gson gson = new Gson();
    ArrendamientosRepository arrendamientosRepository = new ArrendamientosRepository();
    ArrendamientosService arrendamientosService = new ArrendamientosService(arrendamientosRepository);
    String clase = getClass().getSimpleName();
    Boolean isDoPut=false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = (String) request.getAttribute("email");

        String role = (String) request.getAttribute("role");
        Util.logInfo("Se ejecutó DoGet", clase);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        boolean isAsesor=role.equals("Asesor");
        String emailAsesor="";
        if(role.equals("Operación") || role.equals("Administrador") || isAsesor) {
            emailAsesor=isAsesor?email:"";
            try (PrintWriter out = response.getWriter()) {

                JSONArray arrendamientosResult = arrendamientosService.getAll(isAsesor, emailAsesor);

                if (arrendamientosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(arrendamientosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All arrendamientos recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay arrendamientos registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for "+role+" role and sent in response", clase);
                out.flush();
                return;
            } catch (IOException ex) {
                response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
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
                    Arrendamientos newArrendamiento = new Arrendamientos(jsonObject.getBigDecimal("mensualidad")
                            ,jsonObject.getBigDecimal("comision"));
                    newArrendamiento.setNumeroContrato(jsonObject.getString("numeroContrato").toUpperCase());
                    newArrendamiento.setArrendadora(jsonObject.getString("arrendadora"));
                    newArrendamiento.setFechaInicio(jsonObject.getString("fechaInicio"));
                    newArrendamiento.setFechaTermino(jsonObject.getString("fechaTermino"));
                    newArrendamiento.setNumeroMeses(jsonObject.getInt("numeroMeses"));
                    newArrendamiento.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newArrendamiento.setIdVehiculo(jsonObject.getInt("idVehiculo"));

                    Arrendamientos arrendamientoResult = arrendamientosService.add(newArrendamiento);
                    if (arrendamientoResult != null) {
                        if(!(arrendamientoResult.getIdArrendamiento()==-1)){
                        Util.logInfo("Arrendamiento registrado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Arrendamiento registrado exitosamente\"}";
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
                    String errorResponse = "{\"error\": \"El arrendamiento ya existe\"}";
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
                    Arrendamientos newArrendamiento = new Arrendamientos(jsonObject.getBigDecimal("mensualidad")
                            ,jsonObject.getBigDecimal("comision"));
                    newArrendamiento.setIdArrendamiento(jsonObject.getInt("idArrendamiento"));
                    newArrendamiento.setNumeroContrato(jsonObject.getString("numeroContrato").toUpperCase());
                    newArrendamiento.setArrendadora(jsonObject.getString("arrendadora"));
                    newArrendamiento.setFechaInicio(jsonObject.getString("fechaInicio"));
                    newArrendamiento.setFechaTermino(jsonObject.getString("fechaTermino"));
                    newArrendamiento.setNumeroMeses(jsonObject.getInt("numeroMeses"));
                    newArrendamiento.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newArrendamiento.setIdVehiculo(jsonObject.getInt("idVehiculo"));

                    Arrendamientos arrendamientoResult = arrendamientosService.update(newArrendamiento);
                    isDoPut=false;
                    if (arrendamientoResult != null) {
                        Util.logInfo("Arrendamiento modificado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Arrendamiento modificado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Número de contrato ingresado ya está asignado, intentar con otro\"}";
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
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
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

                    if (!Validator.isNumeroContrato(jsonObject.getString("numeroContrato")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Número de contrato inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Arrendamientos arrendamientoToDelete = new Arrendamientos();
                    arrendamientoToDelete.setNumeroContrato(jsonObject.getString("numeroContrato").toUpperCase());

                    if (arrendamientosService.delete(arrendamientoToDelete.getNumeroContrato())) {
                        Util.logInfo("Arrendamiento eliminado correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Arrendamiento eliminado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Arrendamiento no existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }catch (IOException ex) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
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
            sb.append(Validator.isNum("Id Arrendamiento", jsonObject.getString("idArrendamiento"))).append(",");
        }
        sb.append(Validator.isNumeroContrato(jsonObject.getString("numeroContrato"))).append(",");
        sb.append(Validator.isAlpha("Arrendadora", jsonObject.getString("arrendadora"))).append(",");
        sb.append(Validator.isDate("Fecha de inicio",jsonObject.getString("fechaInicio"))).append(",");
        sb.append(Validator.isDate("Fecha de término",jsonObject.getString("fechaTermino"))).append(",");
        sb.append(Validator.isBigDecimal("Mensualidad",jsonObject.getString("mensualidad"))).append(",");
        sb.append(Validator.isBigDecimal("Comisión",jsonObject.getString("comision"))).append(",");
        sb.append(Validator.isNum("Número de meses",String.valueOf(jsonObject.get("numeroMeses")))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Tipo Estatus",String.valueOf(jsonObject.get("idTipoEstatus")))).append(",");
        sb.append(Validator.isNum("Id Vehículo",String.valueOf(jsonObject.get("idVehiculo"))));
        sb.append("}");

        return sb.toString();
    }
}



