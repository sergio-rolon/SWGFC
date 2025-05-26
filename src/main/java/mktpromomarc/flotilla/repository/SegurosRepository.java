package mktpromomarc.flotilla.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import mktpromomarc.flotilla.modelo.Seguros;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SegurosRepository implements ICrudRepository<Seguros>{

    @Override
    public JSONArray findAll(){
        return null;
    }
    public JSONArray findAll(boolean isAsesor, String emailAsesor){
        JSONArray allSeguros = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(isAsesor){
                ps = conn.prepareStatement("SELECT a.\"idSeguro\", a.\"numeroPoliza\", a.\"aseguradora\", " +
                        "a.\"fechaInicio\",a.\"fechaTermino\", a.\"mensualidad\", a.\"comision\", a.\"total\", " +
                        "a.\"totalConIva\",a.\"numeroMeses\", "+
                        "v.\"numeroSerie\"," +
                        "te1.\"tipoEstatus\" AS \"estatusSeguro\", " +
                        "te2.\"tipoEstatus\" AS \"estatusVehiculo\" " +
                        "FROM \"Seguros\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\""+
                        "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                        "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "WHERE u.\"email\" = ? " +
                        "ORDER BY a.\"idSeguro\" ASC"
                );
                ps.setString(1,emailAsesor);
            }else{
                ps = conn.prepareStatement("SELECT a.\"idSeguro\", a.\"numeroPoliza\", a.\"aseguradora\", " +
                        "a.\"fechaInicio\",a.\"fechaTermino\", a.\"mensualidad\", a.\"comision\", a.\"total\", " +
                        "a.\"totalConIva\",a.\"numeroMeses\", "+
                        "v.\"numeroSerie\",v.\"idVehiculo\"," +
                        "te1.\"tipoEstatus\" AS \"estatusSeguro\", " +
                        "te2.\"tipoEstatus\" AS \"estatusVehiculo\" " +
                        "FROM \"Seguros\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\""+
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "ORDER BY a.\"idSeguro\" ASC"
                );
            }
            ResultSet rs = ps.executeQuery();
            allSeguros = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject seguro = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    seguro.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allSeguros.put(seguro);

            }
            Conexion.endConexion(conn);
            return allSeguros;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allSeguros;
    }

    @Override
    public JSONArray findAllObjects() {
        return null;
    }

    @Override
    public Seguros findById(String numeroPoliza){
        Seguros seguro = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Seguros\" where \"numeroPoliza\" = ?");
            ps.setString(1,numeroPoliza);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                seguro = new Seguros();
                seguro.setIdSeguro(rs.getInt("idSeguro"));
                seguro.setNumeroPoliza(rs.getString("numeroPoliza"));
                seguro.setAseguradora(rs.getString("aseguradora"));
                seguro.setFechaInicio(rs.getString("fechaInicio"));
                seguro.setFechaTermino(rs.getString("fechaTermino"));
                seguro.setMensualidad(rs.getBigDecimal("mensualidad"));
                seguro.setComision(rs.getBigDecimal("comision"));
                seguro.setTotal(rs.getBigDecimal("total"));
                seguro.setTotalConIva(rs.getBigDecimal("totalConIva"));
                seguro.setNumeroMeses(rs.getInt("numeroMeses"));
                seguro.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                seguro.setIdVehiculo(rs.getInt("idVehiculo"));
                System.out.println(seguro);
            }
            Conexion.endConexion(conn);
            return seguro;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return seguro;
    }

    @Override
    public boolean existsById(String numeroPoliza){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Seguros\" where \"numeroPoliza\" = ?");
            ps.setString(1,numeroPoliza);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el seguro? "+result);
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
    public Seguros save(Seguros seguro){
        Seguros seguroResult = null;
        // Validar si Seguro.idSeguro es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(seguro.getIdSeguro()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Seguros\" SET " +
                        "\"numeroPoliza\"=?,\"aseguradora\"=?,\"fechaInicio\"=?,\"fechaTermino\"=?,\"mensualidad\"=?," +
                        "\"comision\"=?,\"total\"=?,\"totalConIva\"=?,\"numeroMeses\"=?,\"idTipoEstatus\"=?," +
                        "\"idVehiculo\"=? where \"idSeguro\"=?;");
                ps.setString(1, seguro.getNumeroPoliza());
                ps.setString(2, seguro.getAseguradora());
                ps.setString(3, seguro.getFechaInicio());
                ps.setString(4, seguro.getFechaTermino());
                ps.setBigDecimal(5,seguro.getMensualidad());
                ps.setBigDecimal(6, seguro.getComision());
                ps.setBigDecimal(7, seguro.getTotal());
                ps.setBigDecimal(8, seguro.getTotalConIva());
                ps.setInt(9, seguro.getNumeroMeses());
                ps.setInt(10, seguro.getIdTipoEstatus());
                ps.setInt(11, seguro.getIdVehiculo());
                ps.setInt(12, seguro.getIdSeguro());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return seguro;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Seguros\" (\"idSeguro\", " +
                        "\"numeroPoliza\", \"aseguradora\", \"fechaInicio\",\"fechaTermino\",\"mensualidad\"," +
                        "\"comision\",\"total\",\"totalConIva\",\"numeroMeses\",\"idTipoEstatus\",\"idVehiculo\") " +
                        "VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, seguro.getNumeroPoliza());
                ps.setString(2, seguro.getAseguradora());
                ps.setString(3, seguro.getFechaInicio());
                ps.setString(4, seguro.getFechaTermino());
                ps.setBigDecimal(5,seguro.getMensualidad());
                ps.setBigDecimal(6, seguro.getComision());
                ps.setBigDecimal(7, seguro.getTotal());
                ps.setBigDecimal(8, seguro.getTotalConIva());
                ps.setInt(9, seguro.getNumeroMeses());
                ps.setInt(10, seguro.getIdTipoEstatus());
                ps.setInt(11, seguro.getIdVehiculo());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                seguroResult = findById(seguro.getNumeroPoliza());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return seguroResult;
    }

    @Override
    public boolean deleteById(String numeroPoliza){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Seguros\" where \"numeroPoliza\"=?");
            ps.setString(1, numeroPoliza);

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
    public Seguros findById(int id) {
        Seguros seguro = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Seguros\" where \"idSeguro\" = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                seguro = new Seguros();
                seguro.setIdSeguro(rs.getInt("idSeguro"));
                seguro.setNumeroPoliza(rs.getString("numeroPoliza"));
                seguro.setAseguradora(rs.getString("aseguradora"));
                seguro.setFechaInicio(rs.getString("fechaInicio"));
                seguro.setFechaTermino(rs.getString("fechaTermino"));
                seguro.setMensualidad(rs.getBigDecimal("mensualidad"));
                seguro.setComision(rs.getBigDecimal("comision"));
                seguro.setTotal(rs.getBigDecimal("total"));
                seguro.setTotalConIva(rs.getBigDecimal("totalConIva"));
                seguro.setNumeroMeses(rs.getInt("numeroMeses"));
                seguro.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                seguro.setIdVehiculo(rs.getInt("idVehiculo"));
                System.out.println(seguro);
            }
            Conexion.endConexion(conn);
            return seguro;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return seguro;
    }

    public boolean existByNumeroSerie(int idVehiculo){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Seguros\" where \"idVehiculo\" = ?");
            ps.setInt(1,idVehiculo);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Id vehículo asignado a un seguro? "+result);
            }
            Conexion.endConexion(conn);
            return result;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return result;
    }
}
