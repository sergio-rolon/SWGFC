package org.example.repository;


import org.example.modelo.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Service {

    public static List<Person> getPersons(){

        // Para producción cuando subamos en render
        Connection conn= Conexion.getConexion();

        // Para pruebas en local host
        //Connection conn= Repository.connect_to_db("learningbd2_qs1l","learningbd2","XAvOIRz0M8BulqbYpjbeoW7MQUKelmgq");
        List<Person> persons = new ArrayList<>();

        try{
            PreparedStatement p = conn.prepareStatement("SELECT * FROM public.\"Person\"");
            ResultSet rs = p.executeQuery();

            while(rs.next()){
                Person personTmp = new Person();
                personTmp.setIdPerson(rs.getInt("idPerson"));
                personTmp.setFirstName(rs.getString("firsName"));
                personTmp.setLastName(rs.getString("lastName"));
                personTmp.setDirection(rs.getString("direction"));
                personTmp.setRole(rs.getString("role"));
                personTmp.setPassword(rs.getString("password"));
                System.out.println(personTmp);
                persons.add(personTmp);
            }
            return persons;
        }catch (Exception e){
            System.out.println(e);
        }
        return persons=null;


    }
    /*
    public static void insertPerson(Connection conn){
        try{
            PreparedStatement p = conn.prepareStatement("INSERT INTO public.\"Person\"(\"firsName\", \"lastName\", direction, role, password) VALUES (?, ?, ?, ?, ?)");
            p.setString(1,"Raul");
            p.setString(2,"Cisneros");
            p.setString(3,"Casita");
            p.setString(4,"Asesor");
            p.setString(5,"Password1");
            p.execute();

            System.out.println("Registro exitoso");
        }catch (Exception e){
            System.out.println(e);
        }
    }*/
}
