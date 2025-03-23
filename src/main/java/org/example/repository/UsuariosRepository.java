package org.example.repository;


import org.example.modelo.Usuarios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuariosRepository {


    public static List<Usuarios> findAll(){
        List<Usuarios> allUsuarios = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Usuarios\"");
            ResultSet rs = ps.executeQuery();
            allUsuarios = new ArrayList<>();
            while(rs.next()){
                Usuarios usuario = new Usuarios();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setEmail(rs.getString("email"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellidoPaterno(rs.getString("apellidoPaterno"));
                usuario.setApellidoMaterno(rs.getString("apellidoMaterno"));
                usuario.setNumeroTrabajador(rs.getString("numeroTrabajador"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setIdEstatus(rs.getInt("idEstatus"));
                usuario.setIdTipoUsuario(rs.getInt("idTipoUsuario"));
                System.out.println(usuario);
                allUsuarios.add(usuario);

            }
            Conexion.endConexion(conn);
            return allUsuarios;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allUsuarios;
    }

    public static Usuarios findById(String email){
        Usuarios usuario = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Usuarios\" where \"email\" = ?");
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                usuario = new Usuarios();
                usuario.setIdUsuario(rs.getInt("idUsuario"));
                usuario.setEmail(rs.getString("email"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellidoPaterno(rs.getString("apellidoPaterno"));
                usuario.setApellidoMaterno(rs.getString("apellidoMaterno"));
                usuario.setNumeroTrabajador(rs.getString("numeroTrabajador"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setIdEstatus(rs.getInt("idEstatus"));
                usuario.setIdTipoUsuario(rs.getInt("idTipoUsuario"));
                System.out.println(usuario);
            }
            Conexion.endConexion(conn);
            return usuario;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return usuario;
    }

    public static boolean existsById(String email){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Usuarios\" where \"email\" = ?");
            ps.setString(1,email);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el usuario? "+result);
            }
            Conexion.endConexion(conn);
            return result;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return result;
    }

    public static Usuarios save(Usuarios usuario){
        Usuarios usuarioResult = null;
        // Validar si usuario.idUsuario es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(usuario.getIdUsuario()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Usuarios\" SET " +
                        "\"email\"=?,\"nombre\"=?," +
                        "\"apellidoPaterno\"=?,\"apellidoMaterno\"=?," +
                        "\"numeroTrabajador\"=?,\"contrasena\"=?,\"idEstatus\"=?," +
                        "\"idTipoUsuario\"=? where \"idUsuario\"=?;");
                ps.setString(1, usuario.getEmail());
                ps.setString(2, usuario.getNombre());
                ps.setString(3, usuario.getApellidoPaterno());
                ps.setString(4, usuario.getApellidoMaterno());
                ps.setString(5, usuario.getNumeroTrabajador());
                ps.setString(6, usuario.getContrasena());
                ps.setInt(7, usuario.getIdEstatus());
                ps.setInt(8, usuario.getIdTipoUsuario());
                ps.setInt(9, usuario.getIdUsuario());

                ps.executeUpdate();
                Conexion.endConexion(conn);


                usuarioResult = findById(usuario.getEmail());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Usuarios\" (\"idUsuario\", " +
                        "\"email\", \"nombre\", \"apellidoPaterno\", \"apellidoMaterno\", \"numeroTrabajador\"," +
                        " \"contrasena\", \"idEstatus\", \"idTipoUsuario\") VALUES (DEFAULT,?,?,?,?,?,?,?,?)");
                ps.setString(1, usuario.getEmail());
                ps.setString(2, usuario.getNombre());
                ps.setString(3, usuario.getApellidoPaterno());
                ps.setString(4, usuario.getApellidoMaterno());
                ps.setString(5, usuario.getNumeroTrabajador());
                ps.setString(6, usuario.getContrasena());
                ps.setInt(7, usuario.getIdEstatus());
                ps.setInt(8, usuario.getIdTipoUsuario());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                usuarioResult = findById(usuario.getEmail());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return usuarioResult;
    }

    public static boolean deleteById(String email){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Usuarios\" where \"email\"=?");
            ps.setString(1, email);

            ps.executeUpdate();

            result=true;

            Conexion.endConexion(conn);

        } catch (Exception e) {
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return result;
    }
}
