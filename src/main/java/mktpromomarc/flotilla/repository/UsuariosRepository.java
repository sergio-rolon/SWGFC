package mktpromomarc.flotilla.repository;

import mktpromomarc.flotilla.modelo.Usuarios;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuariosRepository implements CrudRepository<Usuarios>{

    @Override
    public JSONArray findAll(){
        JSONArray allUsuarios = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT u.\"idUsuario\", u.\"email\", " +
                    "u.\"nombre\", u.\"apellidoPaterno\", u.\"apellidoMaterno\", u.\"numeroTrabajador\", u.\"contrasena\", " +
                    "te.\"tipoEstatus\" as \"estatus\", " +
                    "tu.\"tipoUsuario\" as \"tipoUsuario\" FROM \"Usuarios\" u " +
                    "INNER JOIN \"TipoEstatus\" te ON u.\"idTipoEstatus\" = te.\"idTipoEstatus\"" +
                    "INNER JOIN \"TipoUsuario\" tu ON u.\"idTipoUsuario\" = tu.\"idTipoUsuario\""+
                            "ORDER BY u.\"idUsuario\" ASC"
                    );
            ResultSet rs = ps.executeQuery();
            allUsuarios = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject usuario = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    usuario.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allUsuarios.put(usuario);

            }
            Conexion.endConexion(conn);
            return allUsuarios;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allUsuarios;
    }
    @Override
    public JSONArray findAllAsesores(){
        JSONArray allAsesores = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT u.\"idUsuario\", u.\"nombre\", u.\"apellidoPaterno\", u.\"apellidoMaterno\", " +
                    "u.\"numeroTrabajador\" FROM \"Usuarios\" u " +
                    "where \"idTipoUsuario\" = ? AND \"idTipoEstatus\" = ? "+
                    "ORDER BY u.\"numeroTrabajador\" ASC"
            );
            ps.setInt(1,3);
            ps.setInt(2,1);
            ResultSet rs = ps.executeQuery();
            allAsesores = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject usuario = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    usuario.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allAsesores.put(usuario);
            }
            Conexion.endConexion(conn);
            return allAsesores;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allAsesores;
    }

    @Override
    public Usuarios findById(String email){
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
                usuario.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
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

    @Override
    public boolean existsById(String email){
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

    @Override
    public Usuarios save(Usuarios usuario){
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
                        "\"numeroTrabajador\"=?,\"contrasena\"=?,\"idTipoEstatus\"=?," +
                        "\"idTipoUsuario\"=? where \"idUsuario\"=?;");
                ps.setString(1, usuario.getEmail());
                ps.setString(2, usuario.getNombre());
                ps.setString(3, usuario.getApellidoPaterno());
                ps.setString(4, usuario.getApellidoMaterno());
                ps.setString(5, usuario.getNumeroTrabajador());
                ps.setString(6, usuario.getContrasena());
                ps.setInt(7, usuario.getIdTipoEstatus());
                ps.setInt(8, usuario.getIdTipoUsuario());
                ps.setInt(9, usuario.getIdUsuario());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return usuario;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Usuarios\" (\"idUsuario\", " +
                        "\"email\", \"nombre\", \"apellidoPaterno\", \"apellidoMaterno\", \"numeroTrabajador\"," +
                        " \"contrasena\", \"idTipoEstatus\", \"idTipoUsuario\") VALUES (DEFAULT,?,?,?,?,?,?,?,?)");
                ps.setString(1, usuario.getEmail());
                ps.setString(2, usuario.getNombre());
                ps.setString(3, usuario.getApellidoPaterno());
                ps.setString(4, usuario.getApellidoMaterno());
                ps.setString(5, usuario.getNumeroTrabajador());
                ps.setString(6, usuario.getContrasena());
                ps.setInt(7, usuario.getIdTipoEstatus());
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

    @Override
    public boolean deleteById(String email){
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

    @Override
    public boolean deleteById(int id){
        return false;
    }
    @Override
    public Usuarios findById(int id) {
        Usuarios usuario = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Usuarios\" where \"idUsuario\" = ?");
            ps.setInt(1,id);
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
                usuario.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
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
}
