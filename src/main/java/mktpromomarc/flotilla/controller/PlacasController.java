package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Placas;
import mktpromomarc.flotilla.repository.PlacasRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.PlacasService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Placas", urlPatterns = {"/placas/*"})
public class PlacasController extends HttpServlet {
    private Gson gson = new Gson();
    PlacasRepository placasRepository = new PlacasRepository();
    PlacasService placasService = new PlacasService(placasRepository);
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
        if(role.equals("Operación")  || role.equals("Administrador") || isAsesor) {
            emailAsesor=isAsesor?email:"";
            try (PrintWriter out = response.getWriter()) {

                JSONArray placasResult = placasService.getAll(isAsesor, emailAsesor);

                if (placasResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(placasResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All placas recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay placas registrados\"}";
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
                    Placas newPlaca = new Placas(jsonObject.getBigDecimal("costo")
                            ,jsonObject.getBigDecimal("comision"));
                    newPlaca.setSeriePlaca(jsonObject.getString("seriePlaca").toUpperCase());
                    newPlaca.setEstado(jsonObject.getString("estado"));
                    newPlaca.setAnoRenovacion(jsonObject.getInt("anoRenovacion"));
                    newPlaca.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newPlaca.setIdVehiculo(jsonObject.getInt("idVehiculo"));

                    Placas placaResult = placasService.add(newPlaca);
                    if (placaResult != null) {
                        if(!(placaResult.getIdPlaca()==-1)){
                            Util.logInfo("Placa registrado exitosamente", clase);
                            response.setStatus(HttpServletResponse.SC_OK);
                            String successResponse = "{\"success\": \"Placa registrada exitosamente\"}";
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
                    String errorResponse = "{\"error\": \"La placa ya existe\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                } catch (IOException ex) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor");
                }
            } catch (IOException ex) {
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
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,"Error en el servidor");
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
                    Placas newPlaca = new Placas(jsonObject.getBigDecimal("costo")
                            ,jsonObject.getBigDecimal("comision"));
                    newPlaca.setIdPlaca(jsonObject.getInt("idPlaca"));
                    newPlaca.setSeriePlaca(jsonObject.getString("seriePlaca").toUpperCase());
                    newPlaca.setEstado(jsonObject.getString("estado"));
                    newPlaca.setAnoRenovacion(jsonObject.getInt("anoRenovacion"));
                    newPlaca.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newPlaca.setIdVehiculo(jsonObject.getInt("idVehiculo"));

                    Placas placaResult = placasService.update(newPlaca);
                    isDoPut=false;
                    if (placaResult != null) {
                        Util.logInfo("Placa modificado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Placa modificado exitosamente\"}";
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
            } catch (IOException ex) {
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

                    if (!Validator.isSeriePlaca(jsonObject.getString("seriePlaca")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Número de contrato inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Placas placaToDelete = new Placas();
                    placaToDelete.setSeriePlaca(jsonObject.getString("seriePlaca").toUpperCase());

                    if (placasService.delete(placaToDelete.getSeriePlaca())) {
                        Util.logInfo("Placa eliminado correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Placa eliminado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Placa no existe\"}";
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

    private String validateFields(JSONObject jsonObject){
        Validator.validationFailed=false;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if(isDoPut) {
            sb.append(Validator.isNum("Id Placa", jsonObject.getString("idPlaca"))).append(",");
        }
        sb.append(Validator.isSeriePlaca(jsonObject.getString("seriePlaca"))).append(",");
        sb.append(Validator.isAlpha("Estado", jsonObject.getString("estado"))).append(",");
        sb.append(Validator.isBigDecimal("Costo",jsonObject.getString("costo"))).append(",");
        sb.append(Validator.isBigDecimal("Comisión",jsonObject.getString("comision"))).append(",");
        sb.append(Validator.isNum("Año de renovación",String.valueOf(jsonObject.get("anoRenovacion")))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Tipo Estatus",String.valueOf(jsonObject.get("idTipoEstatus")))).append(",");
        sb.append(Validator.isNum("Id Vehículo",String.valueOf(jsonObject.get("idVehiculo"))));
        sb.append("}");

        return sb.toString();
    }
}



