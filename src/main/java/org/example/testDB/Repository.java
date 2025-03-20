package org.example.testDB;

import java.sql.Connection;
import java.sql.DriverManager;

public class Repository {

    public static Connection connect_to_db(String dbname, String user, String pass){
        Connection conn = null;

        try{
            Class.forName("org.postgresql.Driver");

            // Para producción cuando subamos en render
            conn = DriverManager.getConnection("jdbc:postgresql://"+System.getenv("PROD_DB_HOST")+":"+System.getenv("PROD_DB_PORT")+"/"+dbname,user,pass);

            // Para pruebas en local host
            //conn = DriverManager.getConnection("jdbc:postgresql://dpg-cu97k4jqf0us73ddagr0-a.oregon-postgres.render.com:5432/"+dbname,user,pass);
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
}
