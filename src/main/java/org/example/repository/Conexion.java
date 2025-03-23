package org.example.repository;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    public static Connection getConexion(){
        Connection conn = null;

        try{
            Class.forName("org.postgresql.Driver");

            // For production
            conn = DriverManager.getConnection("jdbc:postgresql://"+System.getenv("PROD_DB_HOST")+":"+System.getenv("PROD_DB_PORT")+"/"+System.getenv("PROD_DB_NAME"),System.getenv("PROD_DB_USERNAME"),System.getenv("PROD_DB_PASSWORD"));
            // Para pruebas en local host
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
