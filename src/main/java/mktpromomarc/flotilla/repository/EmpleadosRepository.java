package mktpromomarc.flotilla.repository;

import mktpromomarc.flotilla.controller.EmpleadosController;
import mktpromomarc.flotilla.controller.VehiculosController;
import mktpromomarc.flotilla.modelo.Empleados;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EmpleadosRepository implements ICrudRepository<Empleados>{

    @Override
    public JSONArray findAll(){
        JSONArray allEmpleados = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(EmpleadosController.isAsesor){
                ps = conn.prepareStatement("SELECT e.\"idEmpleado\", e.\"numeroTrabajador\", " +
                        "e.\"nombre\",e.\"apellidoPaterno\", e.\"apellidoMaterno\", e.\"municipioAsignado\"," +
                        "e.\"estadoAsignado\", e.\"cantidadGasolina\", e.\"idCliente\", c.\"rfc\",  " +
                        "c.\"razonSocial\", " +
                        "te1.\"tipoEstatus\" AS \"estatusEmpleado\", " +
                        "te2.\"tipoEstatus\" AS \"estatusCliente\" " +
                        "FROM \"Empleados\" e " +
                        "INNER JOIN \"Clientes\" c ON e.\"idCliente\" = c.\"idCliente\" "+
                        "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" "+
                        "INNER JOIN \"TipoEstatus\" te1 ON e.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON c.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "WHERE u.\"email\" = ? " +
                        "ORDER BY e.\"idEmpleado\" ASC"
                );
                ps.setString(1,EmpleadosController.emailAsesor);

            }else{
                ps = conn.prepareStatement("SELECT e.\"idEmpleado\", e.\"numeroTrabajador\", " +
                    "e.\"nombre\",e.\"apellidoPaterno\", e.\"apellidoMaterno\", e.\"municipioAsignado\"," +
                    "e.\"estadoAsignado\", e.\"cantidadGasolina\", e.\"idCliente\", c.\"rfc\",  " +
                    "c.\"razonSocial\", " +
                    "te1.\"tipoEstatus\" AS \"estatusEmpleado\", " +
                    "te2.\"tipoEstatus\" AS \"estatusCliente\" " +
                    "FROM \"Empleados\" e " +
                    "INNER JOIN \"Clientes\" c ON e.\"idCliente\" = c.\"idCliente\""+
                    "INNER JOIN \"TipoEstatus\" te1 ON e.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                    "INNER JOIN \"TipoEstatus\" te2 ON c.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                    "ORDER BY e.\"idEmpleado\" ASC"
            );
            }
            ResultSet rs = ps.executeQuery();
            allEmpleados = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject empleado = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    empleado.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allEmpleados.put(empleado);

            }
            Conexion.endConexion(conn);
            return allEmpleados;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allEmpleados;
    }

    @Override
    public JSONArray findAllObjects() {
        return null;
    }

    @Override
    public Empleados findById(String numeroTrabajador){
        Empleados empleado = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Empleados\" where \"numeroTrabajador\" = ?");
            ps.setString(1,numeroTrabajador);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                empleado = new Empleados();
                empleado.setIdEmpleado(rs.getInt("idEmpleado"));
                empleado.setNumeroTrabajador(rs.getString("numeroTrabajador"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setApellidoPaterno(rs.getString("apellidoPaterno"));
                empleado.setApellidoMaterno(rs.getString("apellidoMaterno"));
                empleado.setMunicipioAsignado(rs.getString("municipioAsignado"));
                empleado.setEstadoAsignado(rs.getString("estadoAsignado"));
                empleado.setCantidadGasolina(rs.getInt("cantidadGasolina"));
                empleado.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                empleado.setIdCliente(rs.getInt("idCliente"));
                System.out.println(empleado);
            }
            Conexion.endConexion(conn);
            return empleado;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return empleado;
    }

    @Override
    public boolean existsById(String numeroTrabajador){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Empleados\" where \"numeroTrabajador\" = ?");
            ps.setString(1,numeroTrabajador);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el empleado? "+result);
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
    public Empleados save(Empleados empleado){
        Empleados empleadoResult = null;
        // Validar si empleado.idEmpleado es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(empleado.getIdEmpleado()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Empleados\" SET " +
                        "\"numeroTrabajador\"=?,\"nombre\"=?,\"apellidoPaterno\"=?,\"apellidoMaterno\"=?," +
                        "\"municipioAsignado\"=?,\"estadoAsignado\"=?,\"cantidadGasolina\"=?,\"idTipoEstatus\"=?," +
                        "\"idCliente\"=? where \"idEmpleado\"=?;");
                ps.setString(1, empleado.getNumeroTrabajador());
                ps.setString(2, empleado.getNombre());
                ps.setString(3, empleado.getApellidoPaterno());
                ps.setString(4, empleado.getApellidoMaterno());
                ps.setString(5, empleado.getMunicipioAsignado());
                ps.setString(6, empleado.getEstadoAsignado());
                ps.setInt(7, empleado.getCantidadGasolina());
                ps.setInt(8, empleado.getIdTipoEstatus());
                ps.setInt(9, empleado.getIdCliente());
                ps.setInt(10, empleado.getIdEmpleado());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return empleado;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Empleados\" (\"idEmpleado\", " +
                        "\"numeroTrabajador\", \"nombre\", \"apellidoPaterno\",\"apellidoMaterno\"," +
                        "\"municipioAsignado\",\"estadoAsignado\",\"cantidadGasolina\",\"idTipoEstatus\", " +
                        "\"idCliente\") VALUES (DEFAULT,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, empleado.getNumeroTrabajador());
                ps.setString(2, empleado.getNombre());
                ps.setString(3, empleado.getApellidoPaterno());
                ps.setString(4, empleado.getApellidoMaterno());
                ps.setString(5, empleado.getMunicipioAsignado());
                ps.setString(6, empleado.getEstadoAsignado());
                ps.setInt(7, empleado.getCantidadGasolina());
                ps.setInt(8, empleado.getIdTipoEstatus());
                ps.setInt(9, empleado.getIdCliente());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                empleadoResult = findById(empleado.getNumeroTrabajador());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return empleadoResult;
    }

    @Override
    public boolean deleteById(String numeroTrabajador){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Empleados\" where \"numeroTrabajador\"=?");
            ps.setString(1, numeroTrabajador);

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
    public Empleados findById(int id) {
        Empleados empleado = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Empleados\" where \"idEmpleado\" = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                empleado = new Empleados();
                empleado.setIdEmpleado(rs.getInt("idEmpleado"));
                empleado.setNumeroTrabajador(rs.getString("numeroTrabajador"));
                empleado.setApellidoPaterno(rs.getString("apellidoPaterno"));
                empleado.setApellidoMaterno(rs.getString("apellidoMaterno"));
                empleado.setMunicipioAsignado(rs.getString("municipioAsignado"));
                empleado.setEstadoAsignado(rs.getString("estadoAsignado"));
                empleado.setCantidadGasolina(rs.getInt("cantidadGasolina"));
                empleado.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                empleado.setIdCliente(rs.getInt("idCliente"));
                System.out.println(empleado);
            }
            Conexion.endConexion(conn);
            return empleado;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return empleado;
    }
}
