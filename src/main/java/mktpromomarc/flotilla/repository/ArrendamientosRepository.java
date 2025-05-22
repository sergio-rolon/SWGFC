package mktpromomarc.flotilla.repository;

import mktpromomarc.flotilla.controller.ArrendamientosController;
import org.json.JSONArray;
import org.json.JSONObject;
import mktpromomarc.flotilla.modelo.Arrendamientos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ArrendamientosRepository implements ICrudRepository<Arrendamientos>{

    @Override
    public JSONArray findAll(){
        JSONArray allArrendamientos = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(ArrendamientosController.isAsesor){
                ps = conn.prepareStatement("SELECT a.\"idArrendamiento\", a.\"numeroContrato\", a.\"arrendadora\", " +
                        "a.\"fechaInicio\",a.\"fechaTermino\", a.\"mensualidad\", a.\"comision\", a.\"total\", " +
                        "a.\"totalConIva\",a.\"numeroMeses\", "+
                        "v.\"numeroSerie\"," +
                        "te1.\"tipoEstatus\" AS \"estatusArrendamiento\", " +
                        "te2.\"tipoEstatus\" AS \"estatusVehiculo\" " +
                        "FROM \"Arrendamientos\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\""+
                        "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                        "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "WHERE u.\"email\" = ? " +
                        "ORDER BY a.\"idArrendamiento\" ASC"
                );
                ps.setString(1,ArrendamientosController.emailAsesor);
            }else{
                ps = conn.prepareStatement("SELECT a.\"idArrendamiento\", a.\"numeroContrato\", a.\"arrendadora\", " +
                        "a.\"fechaInicio\",a.\"fechaTermino\", a.\"mensualidad\", a.\"comision\", a.\"total\", " +
                        "a.\"totalConIva\",a.\"numeroMeses\", "+
                        "v.\"numeroSerie\"," +
                        "te1.\"tipoEstatus\" AS \"estatusArrendamiento\", " +
                        "te2.\"tipoEstatus\" AS \"estatusVehiculo\" " +
                        "FROM \"Arrendamientos\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\""+
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "ORDER BY a.\"idArrendamiento\" ASC"
                );
            }
            ResultSet rs = ps.executeQuery();
            allArrendamientos = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject Arrendamiento = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    Arrendamiento.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allArrendamientos.put(Arrendamiento);

            }
            Conexion.endConexion(conn);
            return allArrendamientos;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allArrendamientos;
    }

    @Override
    public JSONArray findAllObjects() {
        return null;
    }

    @Override
    public Arrendamientos findById(String numeroContrato){
        Arrendamientos Arrendamiento = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Arrendamientos\" where \"numeroContrato\" = ?");
            ps.setString(1,numeroContrato);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Arrendamiento = new Arrendamientos();
                Arrendamiento.setIdArrendamiento(rs.getInt("idArrendamiento"));
                Arrendamiento.setNumeroContrato(rs.getString("numeroContrato"));
                Arrendamiento.setArrendadora(rs.getString("arrendadora"));
                Arrendamiento.setFechaInicio(rs.getString("fechaInicio"));
                Arrendamiento.setFechaTermino(rs.getString("fechaTermino"));
                Arrendamiento.setMensualidad(rs.getBigDecimal("mensualidad"));
                Arrendamiento.setComision(rs.getBigDecimal("comision"));
                Arrendamiento.setTotal(rs.getBigDecimal("total"));
                Arrendamiento.setTotalConIva(rs.getBigDecimal("totalConIva"));
                Arrendamiento.setNumeroMeses(rs.getInt("numeroMeses"));
                Arrendamiento.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                Arrendamiento.setIdVehiculo(rs.getInt("idVehiculo"));
                System.out.println(Arrendamiento);
            }
            Conexion.endConexion(conn);
            return Arrendamiento;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return Arrendamiento;
    }

    @Override
    public boolean existsById(String numeroContrato){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Arrendamientos\" where \"numeroContrato\" = ?");
            ps.setString(1,numeroContrato);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el arrendamiento? "+result);
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
    public Arrendamientos save(Arrendamientos Arrendamiento){
        Arrendamientos ArrendamientoResult = null;
        // Validar si Arrendamiento.idArrendamiento es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(Arrendamiento.getIdArrendamiento()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Arrendamientos\" SET " +
                        "\"numeroContrato\"=?,\"arrendadora\"=?,\"fechaInicio\"=?,\"fechaTermino\"=?,\"mensualidad\"=?," +
                        "\"comision\"=?,\"total\"=?,\"totalConIva\"=?,\"numeroMeses\"=?,\"idTipoEstatus\"=?," +
                        "\"idVehiculo\"=? where \"idArrendamiento\"=?;");
                ps.setString(1, Arrendamiento.getNumeroContrato());
                ps.setString(2, Arrendamiento.getArrendadora());
                ps.setString(3, Arrendamiento.getFechaInicio());
                ps.setString(4, Arrendamiento.getFechaTermino());
                ps.setBigDecimal(5,Arrendamiento.getMensualidad());
                ps.setBigDecimal(6, Arrendamiento.getComision());
                ps.setBigDecimal(7, Arrendamiento.getTotal());
                ps.setBigDecimal(8, Arrendamiento.getTotalConIva());
                ps.setInt(9, Arrendamiento.getNumeroMeses());
                ps.setInt(10, Arrendamiento.getIdTipoEstatus());
                ps.setInt(11, Arrendamiento.getIdVehiculo());
                ps.setInt(12, Arrendamiento.getIdArrendamiento());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return Arrendamiento;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Arrendamientos\" (\"idArrendamiento\", " +
                        "\"numeroContrato\", \"arrendadora\", \"fechaInicio\",\"fechaTermino\",\"mensualidad\"," +
                        "\"comision\",\"total\",\"totalConIva\",\"numeroMeses\",\"idTipoEstatus\",\"idVehiculo\") " +
                        "VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, Arrendamiento.getNumeroContrato());
                ps.setString(2, Arrendamiento.getArrendadora());
                ps.setString(3, Arrendamiento.getFechaInicio());
                ps.setString(4, Arrendamiento.getFechaTermino());
                ps.setBigDecimal(5,Arrendamiento.getMensualidad());
                ps.setBigDecimal(6, Arrendamiento.getComision());
                ps.setBigDecimal(7, Arrendamiento.getTotal());
                ps.setBigDecimal(8, Arrendamiento.getTotalConIva());
                ps.setInt(9, Arrendamiento.getNumeroMeses());
                ps.setInt(10, Arrendamiento.getIdTipoEstatus());
                ps.setInt(11, Arrendamiento.getIdVehiculo());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                ArrendamientoResult = findById(Arrendamiento.getNumeroContrato());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return ArrendamientoResult;
    }

    @Override
    public boolean deleteById(String numeroContrato){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Arrendamientos\" where \"numeroContrato\"=?");
            ps.setString(1, numeroContrato);

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
    public Arrendamientos findById(int id) {
        Arrendamientos Arrendamiento = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Arrendamientos\" where \"idArrendamiento\" = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Arrendamiento = new Arrendamientos();
                Arrendamiento.setIdArrendamiento(rs.getInt("idArrendamiento"));
                Arrendamiento.setNumeroContrato(rs.getString("numeroContrato"));
                Arrendamiento.setArrendadora(rs.getString("arrendadora"));
                Arrendamiento.setFechaInicio(rs.getString("fechaInicio"));
                Arrendamiento.setFechaTermino(rs.getString("fechaTermino"));
                Arrendamiento.setMensualidad(rs.getBigDecimal("mensualidad"));
                Arrendamiento.setComision(rs.getBigDecimal("comision"));
                Arrendamiento.setTotal(rs.getBigDecimal("total"));
                Arrendamiento.setTotalConIva(rs.getBigDecimal("totalConIva"));
                Arrendamiento.setNumeroMeses(rs.getInt("numeroMeses"));
                Arrendamiento.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                Arrendamiento.setIdVehiculo(rs.getInt("idVehiculo"));
                System.out.println(Arrendamiento);
            }
            Conexion.endConexion(conn);
            return Arrendamiento;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return Arrendamiento;
    }
}
