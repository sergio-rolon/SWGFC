package mktpromomarc.flotilla.controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */

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
import mktpromomarc.flotilla.modelo.Usuarios;
import mktpromomarc.flotilla.repository.UsuariosRepository;
import mktpromomarc.flotilla.security.Encoder;
import mktpromomarc.flotilla.service.UsuariosService;


/**
 *
 * @author Sergio Rolon
 */
@WebFilter(filterName = "FilterLogin", urlPatterns = {"/login","/usuarios/*","/clientes"})
public class FilterLogin implements Filter {

    private static final boolean debug = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;

    public FilterLogin() {
    }

    private static final String secret;

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

        // Write code here to process the request and/or response after
        // the rest of the filter chain is invoked.
        // For example, a logging filter might log the attributes on the
        // request object after the request has been processed.
        /*
	for (Enumeration en = request.getAttributeNames(); en.hasMoreElements(); ) {
	    String name = (String)en.nextElement();
	    Object value = request.getAttribute(name);
	    log("attribute: " + name + "=" + value.toString());

	}
         */
        // For example, a filter might append something to the response.
        /*
	PrintWriter respOut = new PrintWriter(response.getWriter());
	respOut.println("<P><B>This has been appended by an intrusive filter.</B>");
         */
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
        String pathUri = httpServletRequest.getRequestURI();
        System.out.println("Path:"+httpServletRequest.getRequestURI());
        String authHeader =  httpServletRequest.getHeader("Authorization");
        if (  (("POST".equals(httpServletRequest.getMethod())) &&
                (! httpServletRequest.getRequestURI().contains("/login") )  )
                ||
                ("GET".equals(httpServletRequest.getMethod())) ||
                ("PUT".equals(httpServletRequest.getMethod())) ||
                ("DELETE".equals(httpServletRequest.getMethod()))
        ) {
            if(authHeader ==null || !authHeader.startsWith("Bearer: ")) {
                System.out.println("1. Invalid Token");
                ((HttpServletResponse) response).sendRedirect("/pages/login.html");
                return;
                //throw new ServletException("1. Invalid Token");

            }//if authHeader
            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser().setSigningKey(secret)
                        .parseClaimsJws(token).getBody();

                claims.forEach((key, value)-> {
                    System.out.println("key: " + key + "value: " + value);
                });
                //prueba
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                String contrasena = claims.get("contrasena", String.class);
                request.setAttribute("email",email);
                request.setAttribute("role", role);
                UsuariosRepository usuariosRepository = new UsuariosRepository();
                UsuariosService usuariosService = new UsuariosService(usuariosRepository);

                Usuarios registeredUsuario = usuariosService.getById(email);
                if(registeredUsuario==null || !(contrasena.equals(registeredUsuario.getContrasena())
                        && registeredUsuario.getIdEstatus()==1) ) {
                    System.out.println("2. Invalid Token");

                    ((HttpServletResponse) response).sendRedirect("/pages/login.html");
                    return;
                    }

            }catch(SignatureException | MalformedJwtException | ExpiredJwtException e) {
                System.out.println("2. Invalid Token");
                ((HttpServletResponse) response).sendRedirect("/pages/login.html");
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

