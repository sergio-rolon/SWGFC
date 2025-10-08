package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Usuarios;
import mktpromomarc.flotilla.repository.UsuariosRepository;
import mktpromomarc.flotilla.security.Encoder;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.UsuariosService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;

@WebServlet(name = "Usuarios", urlPatterns = {"/usuarios/*"})
public class UsuariosController extends HttpServlet {
    private Gson gson = new Gson();
    UsuariosRepository usuariosRepository = new UsuariosRepository();
    UsuariosService usuariosService = new UsuariosService(usuariosRepository);
    String clase = getClass().getSimpleName();
    Boolean isDoPut=false;
    // In each html page load, JS will call this path to check if user is logged in path: /api/usuarios/logged
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = (String) request.getAttribute("email");
        String role = (String) request.getAttribute("role");
        String requestUrl = request.getRequestURI();
        Util.logInfo("Se ejecutó DoGet con "+requestUrl, clase);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        if(requestUrl.equals("/api/usuarios/logged")){

               try (PrintWriter out = response.getWriter()) {
                       response.setStatus(HttpServletResponse.SC_OK);
                       String successResponse = "{\"email\":\""+email+"\", \"role\":\""+role+"\"}";
                       out.print(successResponse);
                       out.flush();
                       Util.logInfo("User info recovered and sent in response", clase);
                       return;
               } catch (IOException ex) {
                   request.setAttribute("message", "There was an error: " + ex.getMessage());
               }//try
        }//Only for login validation


        if(role.equals("Administrador")) {
                try (PrintWriter out = response.getWriter()) {

                    JSONArray usuariosResult = usuariosService.getAll();

                    if (usuariosResult != null) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        String successResponse = new Gson().toJson(usuariosResult);
                        out.print(successResponse);
                        out.flush();
                        Util.logInfo("All users recovered for admin role and sent in response", clase);
                        return;
                    }
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    String errorResponse = "{\"error\": \"No hay usuarios registrados\"}";
                    out.print(errorResponse);
                    Util.logInfo("None users recovered for admin role and sent in response", clase);
                    out.flush();

                } catch (IOException ex) {
                    request.setAttribute("message", "There was an error: " + ex.getMessage());
                }//try
            }

        if(role.equals("Operación") && requestUrl.equals("/api/usuarios/getAllAsesores")){
            try (PrintWriter out = response.getWriter()) {

                JSONArray usuariosResult = usuariosRepository.findAllObjects();

                if (usuariosResult != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = new Gson().toJson(usuariosResult);
                    out.print(successResponse);
                    out.flush();
                    Util.logInfo("All users recovered for admin role and sent in response", clase);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String errorResponse = "{\"error\": \"No hay usuarios registrados\"}";
                out.print(errorResponse);
                Util.logInfo("None users recovered for admin role and sent in response", clase);
                out.flush();

            } catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }//try

        }

        Util.logInfo("Access denied for user "+email+" with role "+role+" ", clase);
        response.sendRedirect("/index.html");
    }//doGet
        
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Util.logInfo("Se ejecutó DoPost", clase);
        request.setCharacterEncoding("UTF-8");
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
                Usuarios newUser = new Usuarios();
                newUser.setEmail(jsonObject.getString("email"));
                newUser.setNombre(jsonObject.getString("nombre"));
                newUser.setApellidoPaterno(jsonObject.getString("apellidoPaterno"));
                newUser.setApellidoMaterno(jsonObject.getString("apellidoMaterno"));
                newUser.setNumeroTrabajador(jsonObject.getString("numeroTrabajador"));
                newUser.setContrasena(new Encoder().encrypt(jsonObject.getString("contrasena")));
                newUser.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                newUser.setIdTipoUsuario(jsonObject.getInt("idTipoUsuario"));

                Usuarios usuarioResult = usuariosService.add(newUser);
                if (usuarioResult != null) {
                    Util.logInfo("Usuario registrado exitosamente", clase);
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = "{\"success\": \"Usuario registrado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                String errorResponse = "{\"error\": \"El usuario ya existe\"}";
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
        request.setCharacterEncoding("UTF-8");
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
                Usuarios newUser = new Usuarios();
                newUser.setIdUsuario(jsonObject.getInt("idUsuario"));
                newUser.setEmail(jsonObject.getString("email"));
                newUser.setNombre(jsonObject.getString("nombre"));
                newUser.setApellidoPaterno(jsonObject.getString("apellidoPaterno"));
                newUser.setApellidoMaterno(jsonObject.getString("apellidoMaterno"));
                newUser.setNumeroTrabajador(jsonObject.getString("numeroTrabajador"));
                newUser.setContrasena(jsonObject.getString("contrasena"));
                newUser.setIdTipoEstatus(jsonObject.getInt("idTipoEstatus"));
                newUser.setIdTipoUsuario(jsonObject.getInt("idTipoUsuario"));

                Usuarios usuarioResult = usuariosService.update(newUser);
                isDoPut=false;
                if (usuarioResult != null) {
                    Util.logInfo("Usuario modificado exitosamente", clase);
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = "{\"success\": \"Usuario modificado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                String errorResponse = "{\"error\": \"Correo ingresado ya está asignado, intentar con otro\"}";
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
        request.setCharacterEncoding("UTF-8");
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

                if (!Validator.isEmail(jsonObject.getString("email")).contains("success")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    String errorResponse = "{\"error\": \"Email inválido\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }

                Usuarios userToDelete = new Usuarios();
                userToDelete.setEmail(jsonObject.getString("email"));

                if (usuariosService.delete(userToDelete.getEmail())) {
                    Util.logInfo("Usuario eliminado correctamente", clase);
                    response.setStatus(HttpServletResponse.SC_OK);
                    String successResponse = "{\"success\": \"Usuario eliminado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                String errorResponse = "{\"error\": \"Usuario no existe\"}";
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
            sb.append(Validator.isNum("Id Usuario", jsonObject.getString("idUsuario"))).append(",");
        }
        sb.append(Validator.isEmail(jsonObject.getString("email"))).append(",");
        sb.append(Validator.isAlpha("Nombre", jsonObject.getString("nombre"))).append(",");
        sb.append(Validator.isAlpha("Apellido paterno", jsonObject.getString("apellidoPaterno"))).append(",");
        sb.append(Validator.isAlpha("Apellido materno", jsonObject.getString("apellidoMaterno"))).append(",");
        sb.append(Validator.isStringNumeric(jsonObject.getString("numeroTrabajador"))).append(",");
        sb.append(Validator.isAlphaNumSpecial(jsonObject.getString("contrasena"))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Tipo Estatus",String.valueOf(jsonObject.get("idTipoEstatus")))).append(",");
        sb.append(Validator.isNumThreeTypes("Id Tipo Usuario",String.valueOf(jsonObject.get("idTipoUsuario"))));
        sb.append("}");

        return sb.toString();
    }
}



