package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Vehiculos;
import mktpromomarc.flotilla.repository.VehiculosRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.VehiculosService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
/* Bandera aqui en adelante empezaré a hacer cambios de las variables staticas*/
@WebServlet(name = "Vehiculos", urlPatterns = {"/vehiculos/*"})
public class VehiculosController extends HttpServlet {
    private Gson gson = new Gson();
    VehiculosRepository vehiculosRepository = new VehiculosRepository();
    VehiculosService vehiculosService = new VehiculosService(vehiculosRepository);
    String clase = getClass().getSimpleName();
    Boolean isDoPut=false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        String pathInfo=request.getPathInfo() !=null ? request.getPathInfo():"";
        boolean isAsesor = role.equals("asesor");
        String emailAsesor="";
        Util.logInfo("Se ejecutó DoGet", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(role.equals("operacion") && pathInfo.isEmpty() || isAsesor && pathInfo.isEmpty()) {
            emailAsesor=isAsesor?email:"";
            try (PrintWriter out = response.getWriter()) {

                JSONArray vehiculosResult = vehiculosService.getAll(isAsesor,emailAsesor);

                if (vehiculosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(vehiculosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All vehiculos recovered for "+role+" role and sent in response", clase);
                    Util.logInfo(successResponse, clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay vehículos registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for "+role+" role and sent in response", clase);
                out.flush();
                return;
            } catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }
        }
        if(role.equals("operacion") && !pathInfo.isEmpty()){
            try (PrintWriter out = response.getWriter()) {

                JSONArray vehiculosResult = vehiculosRepository.findAllObjects(pathInfo);

                if (vehiculosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(vehiculosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All vehiculos recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay vehículos registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None vehiculos recovered for "+role+" role and sent in response", clase);
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

        if(role.equals("operacion")) {
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

                    String resultValidation = validateFields(jsonObject);
                    Util.logInfo("Resultado validación:"+ resultValidation, clase);
                    if(Validator.validationFailed){
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print(resultValidation);
                        out.flush();
                        return;
                    }
                    Vehiculos newVehiculo = new Vehiculos();
                    newVehiculo.setNumeroSerie(jsonObject.getString("numeroSerie").toUpperCase());
                    newVehiculo.setMarca(jsonObject.getString("marca"));
                    newVehiculo.setTipo(jsonObject.getString("tipo"));
                    newVehiculo.setModelo(jsonObject.getInt("modelo"));
                    newVehiculo.setAccesorios(jsonObject.getString("accesorios"));
                    newVehiculo.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newVehiculo.setIdCliente(jsonObject.getInt("idCliente"));

                    Vehiculos vehiculoResult = vehiculosService.add(newVehiculo);
                    if (vehiculoResult != null) {
                        Util.logInfo("Vehículo registrado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Vehículo registrado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"El vehículo ya existe\"}";
                    out.print(errorResponse);
                    out.flush();
                } catch (IOException ex) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
                }
            } catch (IOException ex) {
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

        if(role.equals("operacion")) {
            try (PrintWriter out = response.getWriter()) {
                String contentType = request.getContentType();
                if (!("application/json".equals(contentType))) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
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
                    Vehiculos newVehiculo = new Vehiculos();
                    newVehiculo.setIdVehiculo(jsonObject.getInt("idVehiculo"));
                    newVehiculo.setNumeroSerie(jsonObject.getString("numeroSerie").toUpperCase());
                    newVehiculo.setMarca(jsonObject.getString("marca"));
                    newVehiculo.setTipo(jsonObject.getString("tipo"));
                    newVehiculo.setModelo(jsonObject.getInt("modelo"));
                    newVehiculo.setAccesorios(jsonObject.getString("accesorios"));
                    newVehiculo.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newVehiculo.setIdCliente(jsonObject.getInt("idCliente"));

                    Vehiculos vehiculoResult = vehiculosService.update(newVehiculo);
                    isDoPut=false;
                    if (vehiculoResult != null) {
                        Util.logInfo("Vehículo modificado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Vehículo modificado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Número de serie ingresado ya está asignado, intentar con otro\"}";
                    out.print(errorResponse);
                    out.flush();
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        Util.logInfo("Se ejecutó DoDelete", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(role.equals("operacion")) {
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

                    if (!Validator.isNumeroSerie(jsonObject.getString("numeroSerie")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Número de serie inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Vehiculos vehiculoToDelete = new Vehiculos();
                    vehiculoToDelete.setNumeroSerie(jsonObject.getString("numeroSerie").toUpperCase());

                    if (vehiculosService.delete(vehiculoToDelete.getNumeroSerie())) {
                        Util.logInfo("Vehículo eliminado correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Vehículo eliminado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Vehículo no existe\"}";
                    out.print(errorResponse);
                    out.flush();
                }catch (IOException e){
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
                }
            }catch (IOException e){
                response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
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
            sb.append(Validator.isNum("Id Vehiculo", jsonObject.getString("idVehiculo"))).append(",");
        }
        sb.append(Validator.isNumeroSerie(jsonObject.getString("numeroSerie"))).append(",");
        sb.append(Validator.isAlphaNum("Marca",jsonObject.getString("marca"))).append(",");
        sb.append(Validator.isAlphaNum("Tipo",jsonObject.getString("tipo"))).append(",");
        sb.append(Validator.isNum("Modelo",jsonObject.getString("modelo"))).append(",");
        sb.append(Validator.isAlphaWithCommas("Accesorios",jsonObject.getString("accesorios"))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Tipo Estatus",String.valueOf(jsonObject.get("idTipoEstatus")))).append(",");
        sb.append(Validator.isNum("Id Cliente",String.valueOf(jsonObject.get("idCliente"))));
        sb.append("}");

        return sb.toString();
    }
}



