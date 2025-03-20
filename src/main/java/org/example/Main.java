package org.example;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public class Main {

    public static final Optional<String> PORT = Optional.ofNullable(System.getenv("PORT"));
    public static final Optional<String> HOSTNAME = Optional.ofNullable(System.getenv("HOSTNAME"));

    public static void main(String[] args) throws LifecycleException {

        //String contextPath = "/myapp";
        // Empty string referencing tu host/index.html
        String contextPath = "";
        String appBase = ".";
        Tomcat tomcat = new Tomcat();
        //define port, host, contextpath
        tomcat.setPort(Integer.valueOf(PORT.orElse("8080")));
        tomcat.setHostname(HOSTNAME.orElse("localhost"));
        tomcat.getHost().setAppBase(appBase);
        tomcat.addWebapp(contextPath,appBase);

        //annotation scanning
        //adding /api to context where dynamic resources are fetched
        Context ctx = tomcat.addContext("/api",new File(".").getAbsolutePath());
        ctx.addLifecycleListener(new ContextConfig());
        // Add the Jar/folder containing this class to PreResources
        final WebResourceRoot root = new StandardRoot(ctx);
        final URL url = findClassLocation(Main.class);
        root.createWebResourceSet(WebResourceRoot.ResourceSetType.PRE, "/WEB-INF/classes", url, "/");
        ctx.setResources(root);

        tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();

    }

    // Tries to find de URL of the JAR or directory  containing {@code clazz}
    private static URL findClassLocation(Class < ? > clazz){
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }
}
