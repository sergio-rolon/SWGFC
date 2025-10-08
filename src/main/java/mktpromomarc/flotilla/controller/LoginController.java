package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.dto.Credentials;
import mktpromomarc.flotilla.repository.UsuariosRepository;
import mktpromomarc.flotilla.security.Encoder;
import mktpromomarc.flotilla.security.Validator;
import mktpromomarc.flotilla.service.UsuariosService;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.dto.Token;
import mktpromomarc.flotilla.modelo.Usuarios;
import mktpromomarc.flotilla.security.JwtGenerator;
import mktpromomarc.flotilla.security.RecaptchaVerifier;

@WebServlet(name = "Login", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {
    private Gson gson = new Gson();
    UsuariosRepository usuariosRepository = new UsuariosRepository();
    UsuariosService usuariosService = new UsuariosService(usuariosRepository);
    String clase = getClass().getSimpleName();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        // Validamos contentype sea correcto
        try (PrintWriter out = response.getWriter()) {
            String contentType = request.getContentType();
            if(!("application/json".equals(contentType))){
                response.sendError(javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Invalid media type"
                        + "content type");
                return;
            }

            // Se crea el jsonObject con la información del body request
            StringBuilder sb = new StringBuilder();
            String line;
            try(BufferedReader reader = request.getReader()){
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String json = sb.toString();
                JSONObject jsonObject = new JSONObject(json);

                // Validamos que el email tenga la forma correcta
                if (!Validator.isEmail(jsonObject.getString("email")).contains("success")) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                    String errorResponse = "{\"error\": \"Usuario o contraseña incorrecto\"}";
                    out.print(errorResponse);
                    out.flush();
                    Util.logInfo("Email invalid, sent in response", clase);
                    return;
                }

                Credentials loginUser = new Credentials();
                loginUser.setToken(jsonObject.getString("token"));
                loginUser.setEmail(jsonObject.getString("email"));
                loginUser.setContrasena(jsonObject.getString("contrasena"));

                //Validamos respuesta del Recaptcha
                if(RecaptchaVerifier.verifyRecaptcha(loginUser.getToken())) {
                    //Recuperamos el Usuario si es que existe
                    Usuarios registeredUsuario = usuariosService.getById(loginUser.getEmail());

                    if(registeredUsuario!=null){
                        // Si existe entonces validamos contraseña
                       if (loginUser.getContrasena().equals(new Encoder().decrypt(registeredUsuario.getContrasena()))
                       && registeredUsuario.getIdTipoEstatus()==1) {
                            String role;
                            // Establecemos rol para el JWToken
                            if(registeredUsuario.getIdTipoUsuario()==1){
                                role="Administrador";
                            }else if(registeredUsuario.getIdTipoUsuario()==2){
                                role="Operación";
                            }else{
                                role="Asesor";
                            }
                            // Generamos token
                           Token tokenGenerated = JwtGenerator.generateToken(registeredUsuario.getEmail(),role,
                                   registeredUsuario.getContrasena());
                           String tokenResponseString = new Gson().toJson(tokenGenerated);

                           System.out.println(tokenResponseString);

                           // Se genera la respuesta

                           out.print(tokenResponseString);
                           out.flush();
                           Util.logInfo("JWT generated for user "+registeredUsuario.getEmail()+" with role "+
                                   role+" and status "+registeredUsuario.getIdTipoEstatus()+"logged in", clase);
                        } else {
                           response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                           String errorResponse = "{\"error\": \"Usuario o contraseña incorrecto\"}";
                           out.print(errorResponse);
                           out.flush();
                           Util.logInfo("Email invalid, sent in response", clase);
                        }//ifPassword
                    }else{
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                        String errorResponse = "{\"error\": \"Usuario o contraseña incorrecto\"}";
                        out.print(errorResponse);
                        out.flush();
                        Util.logInfo("Email invalid, sent in response", clase);
                    }//ifExists
                }//ifRecaptcha
                    else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String errorResponse = "{\"error\": \"Recaptcha detectó actividad inusual.\"}";

                out.print(errorResponse);
                Util.logInfo("Invalid recaptcha, sent in response", clase);
                out.flush();
                }
            }catch (IOException ex) {
                request.setAttribute("message", "There was an error: " + ex.getMessage());
            }
        } catch (IOException ex){
            request.setAttribute("message", "There was an error: "+ex.getMessage());
        }
    }

}
