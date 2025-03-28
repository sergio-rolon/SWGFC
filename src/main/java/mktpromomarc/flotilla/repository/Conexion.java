package mktpromomarc.flotilla.repository;

import mktpromomarc.flotilla.config.ConfigLoader;
import mktpromomarc.flotilla.config.Util;
import mktpromomarc.flotilla.modelo.Usuarios;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {



    private static final String db_host;
    private static final String db_name;
    private static final String db_port;
    private static final String db_username;
    private static final String db_password;

    static String clase = Conexion.class.getSimpleName();

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
            Util.logInfo("Connection to database OPEN", clase);
        }else{
            Util.logInfo("Connection to dabase FAILED", clase);
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
                Util.logInfo("Connection to database CLOSE", clase);
            }catch (Throwable t){

            }
        }
    }
}
