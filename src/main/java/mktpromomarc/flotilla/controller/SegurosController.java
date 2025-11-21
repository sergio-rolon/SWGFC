package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Seguros;
import mktpromomarc.flotilla.repository.SegurosRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.SegurosService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Seguros", urlPatterns = {"/seguros/*"})
public class SegurosController extends HttpServlet {
    private Gson gson = new Gson();
    SegurosRepository segurosRepository = new SegurosRepository();
    SegurosService segurosService = new SegurosService(segurosRepository);
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

                JSONArray segurosResult = segurosService.getAll(isAsesor, emailAsesor);

                if (segurosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(segurosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All seguros recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay seguros registrados\"}";
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
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,"Error en el servidor");
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
                    Seguros newSeguro = new Seguros(jsonObject.getBigDecimal("mensualidad")
                            ,jsonObject.getBigDecimal("comision"));
                    newSeguro.setNumeroPoliza(jsonObject.getString("numeroPoliza").toUpperCase());
                    newSeguro.setAseguradora(jsonObject.getString("aseguradora"));
                    newSeguro.setFechaInicio(jsonObject.getString("fechaInicio"));
                    newSeguro.setFechaTermino(jsonObject.getString("fechaTermino"));
                    newSeguro.setNumeroMeses(jsonObject.getInt("numeroMeses"));
                    newSeguro.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newSeguro.setIdVehiculo(jsonObject.getInt("idVehiculo"));

                    Seguros seguroResult = segurosService.add(newSeguro);
                    if (seguroResult != null) {
                        if(!(seguroResult.getIdSeguro()==-1)){
                            Util.logInfo("Seguro registrado exitosamente", clase);
                            response.setStatus(HttpServletResponse.SC_OK);
                            String successResponse = "{\"success\": \"Seguro registrado exitosamente\"}";
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
                    String errorResponse = "{\"error\": \"El seguro ya existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                } catch (IOException ex) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
                }
            }catch (IOException ex){
                response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
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
                    Seguros newSeguro = new Seguros(jsonObject.getBigDecimal("mensualidad")
                            ,jsonObject.getBigDecimal("comision"));
                    newSeguro.setIdSeguro(jsonObject.getInt("idSeguro"));
                    newSeguro.setNumeroPoliza(jsonObject.getString("numeroPoliza").toUpperCase());
                    newSeguro.setAseguradora(jsonObject.getString("aseguradora"));
                    newSeguro.setFechaInicio(jsonObject.getString("fechaInicio"));
                    newSeguro.setFechaTermino(jsonObject.getString("fechaTermino"));
                    newSeguro.setNumeroMeses(jsonObject.getInt("numeroMeses"));
                    newSeguro.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newSeguro.setIdVehiculo(jsonObject.getInt("idVehiculo"));

                    Seguros seguroResult = segurosService.update(newSeguro);
                    isDoPut=false;
                    if (seguroResult != null) {
                        Util.logInfo("Seguro modificado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Seguro modificado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Número de contrato ingresado ya está asignado, intentar con otro\"}";
                    out.print(errorResponse);
                    out.flush();
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

                    if (!Validator.isNumeroPoliza(jsonObject.getString("numeroPoliza")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Número de contrato inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Seguros seguroToDelete = new Seguros();
                    seguroToDelete.setNumeroPoliza(jsonObject.getString("numeroPoliza").toUpperCase());

                    if (segurosService.delete(seguroToDelete.getNumeroPoliza())) {
                        Util.logInfo("Seguro eliminado correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Seguro eliminado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Seguro no existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }
            } catch (IOException ex) {
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
            sb.append(Validator.isNum("Id Seguro", jsonObject.getString("idSeguro"))).append(",");
        }
        sb.append(Validator.isNumeroPoliza(jsonObject.getString("numeroPoliza"))).append(",");
        sb.append(Validator.isAlpha("Aseguradora", jsonObject.getString("aseguradora"))).append(",");
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



