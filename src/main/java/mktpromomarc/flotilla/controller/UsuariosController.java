package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (PrintWriter out = response.getWriter()) {

            JSONArray usuariosResult = usuariosService.getAll();

            if (usuariosResult != null) {
                System.out.println("Usuarios recuperados");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String successResponse = new Gson().toJson(usuariosResult);
                out.print(successResponse);
                out.flush();
                return;
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String errorResponse = "{\"error\": \"No hay usuarios registrados\"}";
            out.print(errorResponse);
            out.flush();

        } catch (IOException ex) {
            request.setAttribute("message", "There was an error: " + ex.getMessage());
        }
        }
        
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Se ejecuto doPost");
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

                String resultValidation = usuariosValidator(jsonObject);
                System.out.println("Resultado validación:"+ resultValidation);
                if(Validator.validationFailed){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
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
                newUser.setIdEstatus(jsonObject.getInt("idEstatus"));
                newUser.setIdTipoUsuario(jsonObject.getInt("idTipoUsuario"));

                Usuarios usuarioResult = usuariosService.add(newUser);
                if (usuarioResult != null) {
                    System.out.println("Usuario agregado correctamente");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String successResponse = "{\"success\": \"Usuario registrado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String errorResponse = "{\"error\": \"Usuario ya existe\"}";
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
        System.out.println("Se ejecuto doPut");
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

                String resultValidation = usuariosValidator(jsonObject);
                System.out.println("Resultado validación:"+ resultValidation);
                if(Validator.validationFailed){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
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
                newUser.setIdEstatus(jsonObject.getInt("idEstatus"));
                newUser.setIdTipoUsuario(jsonObject.getInt("idTipoUsuario"));

                Usuarios usuarioResult = usuariosService.update(newUser);
                if (usuarioResult != null) {
                    System.out.println("Usuario agregado correctamente");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String successResponse = "{\"success\": \"Usuario modificado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String errorResponse = "{\"error\": \"Usuario no existe\"}";
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
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String errorResponse = "{\"error\": \"Email inválido\"}";
                    out.print(errorResponse);
                    out.flush();
                    return;
                }

                Usuarios deleteUser = new Usuarios();
                deleteUser.setEmail(jsonObject.getString("email"));


                if (usuariosService.delete(deleteUser.getEmail())) {
                    System.out.println("Usuario eliminado correctamente");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String successResponse = "{\"success\": \"Usuario eliminado exitosamente\"}";
                    out.print(successResponse);
                    out.flush();
                    return;
                }
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String errorResponse = "{\"error\": \"Usuario no existe\"}";
                out.print(errorResponse);
                out.flush();
            }
        }
    }

    private String usuariosValidator(JSONObject jsonObject){
        Validator.validationFailed=false;
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append(Validator.isEmail(jsonObject.getString("email"))).append(",");
        sb.append(Validator.isAlpha("Nombre", jsonObject.getString("nombre"))).append(",");
        sb.append(Validator.isAlpha("Apellido Paterno", jsonObject.getString("apellidoPaterno"))).append(",");
        sb.append(Validator.isAlpha("Apellido Materno", jsonObject.getString("apellidoMaterno"))).append(",");
        sb.append(Validator.isStringNumeric(jsonObject.getString("numeroTrabajador"))).append(",");
        sb.append(Validator.isAlphaNumSpecial(jsonObject.getString("contrasena"))).append(",");
        sb.append(Validator.isNumTwoTypes("Id Estatus",String.valueOf(jsonObject.get("idEstatus")))).append(",");
        sb.append(Validator.isNumThreeTypes("Id Tipo Usuario",String.valueOf(jsonObject.get("idTipoUsuario"))));
        sb.append("}");

        return sb.toString();
    }
}



