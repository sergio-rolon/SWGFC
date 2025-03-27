package mktpromomarc.flotilla.controller;

import com.google.gson.Gson;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    String errorResponse = "{\"error\": \"Usuario o contraseña incorrecto\"}";
                    out.print(errorResponse);
                    out.flush();
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
                       && registeredUsuario.getIdEstatus()==1) {

                            System.out.println("Usuario logeado correctamente");

                            String rol;
                            // Establecemos rol para el JWToken
                           //todo probably this is goint to be changes to .contains("administrador")
                            if(registeredUsuario.getIdTipoUsuario()==1){
                                rol="administrador";
                            }else if(registeredUsuario.getIdTipoUsuario()==2){
                                rol="operacion";
                            }else{
                                rol="asesor";
                            }
                            // Generamos token
                           Token tokenGenerated = JwtGenerator.generateToken(registeredUsuario.getEmail(),rol,
                                   registeredUsuario.getContrasena());
                           String tokenResponseString = new Gson().toJson(tokenGenerated);

                           System.out.println(tokenResponseString);

                           // Se genera la respuesta
                           response.setContentType("application/json");
                           response.setCharacterEncoding("UTF-8");
                           out.print(tokenResponseString);
                           out.flush();
                        } else {
                           String tokenResponseString = new Gson().toJson(new Token("false","true"));

                           response.setContentType("application/json");
                           response.setCharacterEncoding("UTF-8");
                           out.print(tokenResponseString);
                           out.flush();

                            System.out.println("Usuario o contraseña incorrecto");
                        }//ifPassword
                    }else{
                        String tokenResponseString = new Gson().toJson(new Token("false","true"));

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");
                        out.print(tokenResponseString);
                        out.flush();

                        System.out.println("Usuario o contraseña incorrecto");
                    }//ifExists
                }//ifRecaptcha
                else{
     /* To do cambiar la response indicando falla de captcha y correcta bad request o malformed*/
            String tokenResponseString = new Gson().toJson(new Token("false","false"));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(tokenResponseString);
            out.flush();

            }
            }

        } catch (IOException ex){
            request.setAttribute("message", "There was an error: "+ex.getMessage());
        }
    }

}
