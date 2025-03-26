package mktpromomarc.flotilla.repository;


import mktpromomarc.flotilla.modelo.Clientes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClientesRepository implements CrudRepository<Clientes> {


    @Override
    public JSONArray findAll(){
        JSONArray allClientes = null;

        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT c.\"idCliente\", c.\"razonSocial\", " +
                    "c.\"rfc\", c.\"idEstatus\" as \"estatusCliente\", u.\"idUsuario\", u.\"email\", " +
                    "u.\"nombre\", u.\"apellidoPaterno\", u.\"apellidoMaterno\", u.\"numeroTrabajador\", " +
                    "u.\"contrasena\", u.\"idEstatus\" as \"estatusUsuario\", " +
                    "u.\"idTipoUsuario\" FROM \"Clientes\" c INNER JOIN \"Usuarios\" " +
                    "u ON c.\"idUsuario\" = u.\"idUsuario\"");
            ResultSet rs = ps.executeQuery();
            allClientes = new JSONArray();
            while(rs.next()){
            int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject cliente = new JSONObject();
            for(int i=0; i<totalColumns;i++){
                cliente.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
            }
            allClientes.put(cliente);

            }
            Conexion.endConexion(conn);
            return allClientes;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allClientes;
    }
    @Override
    public Clientes findById(String rfc){
        Clientes cliente = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Clientes\" where \"rfc\" = ?");
            ps.setString(1,rfc);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                cliente = new Clientes();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setRazonSocial(rs.getString("razonSocial"));
                cliente.setRfc(rs.getString("rfc"));
                cliente.setIdEstatus(rs.getInt("idEstatus"));
                cliente.setIdUsuario(rs.getInt("idUsuario"));
                System.out.println(cliente);
            }
            Conexion.endConexion(conn);
            return cliente;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return cliente;
    }
    @Override
    public boolean existsById(String rfc){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Clientes\" where \"rfc\" = ?");
            ps.setString(1,rfc);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el cliente? "+result);
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
    public Clientes save(Clientes cliente){
        Clientes clienteResult = null;
        // Validar si cliente.idUsuario es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(cliente.getIdCliente()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Clientes\" SET " +
                        "\"razonSocial\"=?,\"idEstatus\"=?,\"idUsuario\"=? where \"idCliente\"=?;");
                ps.setString(1, cliente.getRazonSocial());
                ps.setInt(2, cliente.getIdEstatus());
                ps.setInt(3, cliente.getIdUsuario());
                ps.setInt(4, cliente.getIdCliente());

                ps.executeUpdate();
                Conexion.endConexion(conn);


                clienteResult = findById(cliente.getRfc());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Clientes\" (\"idCliente\", " +
                        "\"razonSocial\", \"rfc\", \"idEstatus\", \"idUsuario\") VALUES (DEFAULT,?,?,?,?)");
                ps.setString(1, cliente.getRazonSocial());
                ps.setString(2, cliente.getRfc());
                ps.setInt(3, cliente.getIdEstatus());
                ps.setInt(4, cliente.getIdUsuario());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                clienteResult = findById(cliente.getRfc());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return clienteResult;
    }
    @Override
    public boolean deleteById(String rfc){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Clientes\" where \"rfc\"=?");
            ps.setString(1, rfc);

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
    public Clientes findById(int id) {
        return new Clientes();
    }
}
