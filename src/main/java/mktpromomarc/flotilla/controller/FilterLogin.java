package mktpromomarc.flotilla.controller;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import mktpromomarc.flotilla.config.ConfigLoader;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Usuarios;
import mktpromomarc.flotilla.repository.UsuariosRepository;
import mktpromomarc.flotilla.service.UsuariosService;

@WebFilter(filterName = "FilterLogin", urlPatterns = {"/login","/usuarios/*","/clientes"})
public class FilterLogin implements Filter {

    private static final boolean debug = true;

    private FilterConfig filterConfig = null;

    public FilterLogin() {
    }

    private static final String secret;
    String clase = getClass().getSimpleName();

    static{
        ConfigLoader configLoader = ConfigLoader.getInstance();
        if(!configLoader.isEmpty()){
            // Para pruebas en local host
            secret = configLoader.getProperty("filterlogin.secret");
        }else {
            // For production
            secret = System.getenv("PROD_FL_SECRET");
        }
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("FilterLogin:DoBeforeProcessing");
        }
    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("FilterLogin:DoAfterProcessing");
        }
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String authHeader =  httpServletRequest.getHeader("Authorization");
        if (  (("POST".equals(httpServletRequest.getMethod())) &&
                (! httpServletRequest.getRequestURI().contains("/login") )  )
                ||
                ("GET".equals(httpServletRequest.getMethod())) ||
                ("PUT".equals(httpServletRequest.getMethod())) ||
                ("DELETE".equals(httpServletRequest.getMethod()))
        ) {
            if(authHeader ==null || !authHeader.startsWith("Bearer: ")) {
                Util.logInfo("Invalid token, missing bearear or null authheader", clase);
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED); // o SC_FORBIDDEN
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Token inválido o no autorizado\"}");
                return;

            }//if authHeader
            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser().setSigningKey(secret)
                        .parseClaimsJws(token).getBody();
                //prueba
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                String contrasena = claims.get("contrasena", String.class);
                request.setAttribute("email",email);
                request.setAttribute("role", role);
                Util.logInfo("JWT= role: "+role+" email: "+email, clase);

                UsuariosRepository usuariosRepository = new UsuariosRepository();
                UsuariosService usuariosService = new UsuariosService(usuariosRepository);

                Usuarios registeredUsuario = usuariosService.getById(email);

                if(registeredUsuario==null || !(contrasena.equals(registeredUsuario.getContrasena())
                        && !(registeredUsuario.getIdEstatus()==1)) ) {
                    Util.logInfo("Invalid token, user null, password invalid or status inactive", clase);
                    ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED); // o SC_FORBIDDEN
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Token inválido o no autorizado\"}");
                    return;
                    }

            }catch(SignatureException | MalformedJwtException | ExpiredJwtException e) {
                Util.logInfo("Invalid token, wrong signature, expired or malformed JWT", clase);
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED); // o SC_FORBIDDEN
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Token inválido o no autorizado\"}");
                return;
                //throw new ServletException("2. Invalid Token");
            }//catch
        }//if getMethod
        chain.doFilter(request, response);
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("FilterLogin:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("FilterLogin()");
        }
        StringBuffer sb = new StringBuffer("FilterLogin(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}

