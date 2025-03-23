package org.example.repository;

import org.example.config.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    // TO-DO pendiente actualizar con config.properties
    private static final String db_host;
    private static final String db_name;
    private static final String db_port;
    private static final String db_username;
    private static final String db_password;

    static{
        ConfigLoader configLoader = ConfigLoader.getInstance();
        if(!configLoader.isEmpty()){
            // Para pruebas en local host
            db_host = configLoader.getProperty("conexion.db_host");
            db_name = configLoader.getProperty("conexion.db_name");
            db_port = configLoader.getProperty("conexion.db_port");
            db_username = configLoader.getProperty("conexion.username");
            db_password = configLoader.getProperty("conexion.password");
        }else {
            // For production
            db_host = System.getenv("PROD_DB_HOST");
            db_name = System.getenv("PROD_DB_NAME");
            db_port = System.getenv("PROD_DB_PORT");
            db_username = System.getenv("PROD_DB_USERNAME");
            db_password = System.getenv("PROD_DB_PASSWORD");
        }
    }
    public static Connection getConexion(){
        Connection conn = null;

        try{
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection("jdbc:postgresql://"+db_host+":"+db_port+"/"+db_name,db_username,db_password);

        if(conn!=null){
            System.out.println("Connection Established");
        }else{
            System.out.println("Connection Failed");
        }

        }catch (Exception e){
            System.out.println(e);
        }
        return conn;
    }

    public static void endConexion(Connection conn){
        if(conn != null){
            try{
                conn.close();
            }catch (Throwable t){

            }
        }
    }
}
