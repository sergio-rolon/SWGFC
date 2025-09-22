package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Empleados;
import mktpromomarc.flotilla.repository.EmpleadosRepository;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.EmpleadosService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Empleados", urlPatterns = {"/empleados/*"})
public class EmpleadosController extends HttpServlet {
    private Gson gson = new Gson();
    EmpleadosRepository empleadosRepository = new EmpleadosRepository();
    EmpleadosService empleadosService = new EmpleadosService(empleadosRepository);
    String clase = getClass().getSimpleName();
    Boolean isDoPut=false;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        String pathInfo=request.getPathInfo() !=null ? request.getPathInfo():"";
        Util.logInfo("Se ejecutó DoGet", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        boolean isAsesor=role.equals("asesor");
        String emailAsesor="";
        if(role.equals("operacion") || isAsesor) {
            emailAsesor=isAsesor?email:"";

            try (PrintWriter out = response.getWriter()) {

                JSONArray empleadosResult = empleadosService.getAll(isAsesor,emailAsesor);

                if (empleadosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(empleadosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All empleados recovered for "+role+" role and sent in response", clase);
                    Util.logInfo(successResponse, clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay empleados registrados\"}";
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

                JSONArray empleadosResult = empleadosRepository.findAllObjects(pathInfo);

                if (empleadosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(empleadosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All empleados recovered for "+role+" role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay empleados registrados\"}";
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

        if(role.equals("asesor")) {
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
                    Empleados newEmpleado = new Empleados();
                    newEmpleado.setNumeroTrabajador(jsonObject.getString("numeroTrabajador").toUpperCase());
                    newEmpleado.setNombre(jsonObject.getString("nombre"));
                    newEmpleado.setApellidoPaterno(jsonObject.getString("apellidoPaterno"));
                    newEmpleado.setApellidoMaterno(jsonObject.getString("apellidoMaterno"));
                    newEmpleado.setMunicipioAsignado(jsonObject.getString("municipioAsignado"));
                    newEmpleado.setEstadoAsignado(jsonObject.getString("estadoAsignado"));
                    newEmpleado.setCantidadGasolina(jsonObject.getInt("cantidadGasolina"));
                    newEmpleado.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newEmpleado.setIdCliente(jsonObject.getInt("idCliente"));

                    Empleados empleadoResult = empleadosService.add(newEmpleado);
                    if (empleadoResult != null) {
                        Util.logInfo("Empleado registrado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Empleado registrado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"El empleado ya existe\"}";
                    out.print(errorResponse);
                    out.flush();
                } catch (IOException ex) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
                }
            } catch (IOException e) {
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

        if(role.equals("asesor")) {
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
                    Empleados newEmpleado = new Empleados();
                    newEmpleado.setIdEmpleado(jsonObject.getInt("idEmpleado"));
                    newEmpleado.setNumeroTrabajador(jsonObject.getString("numeroTrabajador").toUpperCase());
                    newEmpleado.setNombre(jsonObject.getString("nombre"));
                    newEmpleado.setApellidoPaterno(jsonObject.getString("apellidoPaterno"));
                    newEmpleado.setApellidoMaterno(jsonObject.getString("apellidoMaterno"));
                    newEmpleado.setMunicipioAsignado(jsonObject.getString("municipioAsignado"));
                    newEmpleado.setEstadoAsignado(jsonObject.getString("estadoAsignado"));
                    newEmpleado.setCantidadGasolina(jsonObject.getInt("cantidadGasolina"));
                    newEmpleado.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                    newEmpleado.setIdCliente(jsonObject.getInt("idCliente"));


                    Empleados empleadoResult = empleadosService.update(newEmpleado);
                    isDoPut=false;
                    if (empleadoResult != null) {
                        Util.logInfo("Empleado modificado exitosamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Empleado modificado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Número de trabajador ingresado ya está asignado, intentar con otro\"}";
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
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        Util.logInfo("Se ejecutó DoDelete", clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(role.equals("asesor")) {
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

                    if (!Validator.isStringNumeric(jsonObject.getString("numeroTrabajador")).contains("success")) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String errorResponse = "{\"error\": \"Número de trabajador inválido\"}";
                        out.print(errorResponse);
                        out.flush();
                        return;
                    }

                    Empleados empleadoToDelete = new Empleados();
                    empleadoToDelete.setNumeroTrabajador(jsonObject.getString("numeroTrabajador"));

                    if (empleadosService.delete(empleadoToDelete.getNumeroTrabajador())) {
                        Util.logInfo("Empleado eliminado correctamente", clase);
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = "{\"success\": \"Empleado eliminado exitosamente\"}";
                        out.print(successResponse);
                        out.flush();
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    String errorResponse = "{\"error\": \"Empleado no existe\"}";
                    out.print(errorResponse);
                    out.flush();
                } catch (IOException e) {
                    response.sendError(javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Error en el servidor");
                }
            } catch (IOException e) {
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
            sb.append(Validator.isNum("Id Empleado", jsonObject.getString("idEmpleado"))).append(",");
        }
        sb.append(Validator.isStringNumeric(jsonObject.getString("numeroTrabajador"))).append(",");
        sb.append(Validator.isAlpha("Nombre", jsonObject.getString("nombre"))).append(",");
        sb.append(Validator.isAlpha("Apellido paterno", jsonObject.getString("apellidoPaterno"))).append(",");
        sb.append(Validator.isAlpha("Apellido materno", jsonObject.getString("apellidoMaterno"))).append(",");
        sb.append(Validator.isAlpha("Municipio asignado", jsonObject.getString("municipioAsignado"))).append(",");
        sb.append(Validator.isAlpha("Estado asignado", jsonObject.getString("estadoAsignado"))).append(",");
        sb.append(Validator.isNum("Cantidad gasolina",jsonObject.getString("cantidadGasolina"))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Tipo Estatus",String.valueOf(jsonObject.get("idTipoEstatus")))).append(",");
        sb.append(Validator.isNum("Id Cliente",String.valueOf(jsonObject.get("idCliente"))));
        sb.append("}");

        return sb.toString();
    }
}



