package mktpromomarc.flotilla.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import mktpromomarc.flotilla.modelo.Placas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlacasRepository implements ICrudRepository<Placas>{

    @Override
    public JSONArray findAll(){
        return null;
    }
    public JSONArray findAll(boolean isAsesor, String emailAsesor){
        JSONArray allPlacas = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(isAsesor){
                ps = conn.prepareStatement("SELECT a.\"idPlaca\", a.\"seriePlaca\", a.\"estado\", " +
                        "a.\"costo\", a.\"comision\", a.\"total\", " +
                        "a.\"totalConIva\",a.\"anoRenovacion\", "+
                        "v.\"numeroSerie\"," +
                        "te1.\"tipoEstatus\" AS \"estatusPlaca\", " +
                        "te2.\"tipoEstatus\" AS \"estatusVehiculo\" " +
                        "FROM \"Placas\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\""+
                        "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                        "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "WHERE u.\"email\" = ? " +
                        "ORDER BY a.\"idPlaca\" ASC"
                );
                ps.setString(1,emailAsesor);
            }else{
                ps = conn.prepareStatement("SELECT a.\"idPlaca\", a.\"seriePlaca\", a.\"estado\", " +
                        "a.\"costo\", a.\"comision\", a.\"total\", " +
                        "a.\"totalConIva\",a.\"anoRenovacion\", "+
                        "v.\"numeroSerie\",v.\"idVehiculo\"," +
                        "te1.\"tipoEstatus\" AS \"estatusPlaca\", " +
                        "te2.\"tipoEstatus\" AS \"estatusVehiculo\" " +
                        "FROM \"Placas\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\""+
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "ORDER BY a.\"idPlaca\" ASC"
                );
            }
            ResultSet rs = ps.executeQuery();
            allPlacas = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject placa = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    placa.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allPlacas.put(placa);

            }
            Conexion.endConexion(conn);
            return allPlacas;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allPlacas;
    }

    @Override
    public JSONArray findAllObjects() {
        return null;
    }

    @Override
    public Placas findById(String seriePlaca){
        Placas placa = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Placas\" where \"seriePlaca\" = ?");
            ps.setString(1,seriePlaca);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                placa = new Placas();
                placa.setIdPlaca(rs.getInt("idPlaca"));
                placa.setSeriePlaca(rs.getString("seriePlaca"));
                placa.setEstado(rs.getString("estado"));
                placa.setCosto(rs.getBigDecimal("costo"));
                placa.setComision(rs.getBigDecimal("comision"));
                placa.setTotal(rs.getBigDecimal("total"));
                placa.setTotalConIva(rs.getBigDecimal("totalConIva"));
                placa.setAnoRenovacion(rs.getInt("anoRenovacion"));
                placa.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                placa.setIdVehiculo(rs.getInt("idVehiculo"));
                System.out.println(placa);
            }
            Conexion.endConexion(conn);
            return placa;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return placa;
    }

    @Override
    public boolean existsById(String seriePlaca){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Placas\" where \"seriePlaca\" = ?");
            ps.setString(1,seriePlaca);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el placa? "+result);
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
    public Placas save(Placas Placa){
        Placas PlacaResult = null;
        // Validar si Placa.idPlaca es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(Placa.getIdPlaca()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Placas\" SET " +
                        "\"seriePlaca\"=?,\"estado\"=?,\"costo\"=?," +
                        "\"comision\"=?,\"total\"=?,\"totalConIva\"=?,\"anoRenovacion\"=?,\"idTipoEstatus\"=?," +
                        "\"idVehiculo\"=? where \"idPlaca\"=?;");
                ps.setString(1, Placa.getSeriePlaca());
                ps.setString(2, Placa.getEstado());
                ps.setBigDecimal(3,Placa.getCosto());
                ps.setBigDecimal(4, Placa.getComision());
                ps.setBigDecimal(5, Placa.getTotal());
                ps.setBigDecimal(6, Placa.getTotalConIva());
                ps.setInt(7, Placa.getAnoRenovacion());
                ps.setInt(8, Placa.getIdTipoEstatus());
                ps.setInt(9, Placa.getIdVehiculo());
                ps.setInt(10, Placa.getIdPlaca());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return Placa;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Placas\" (\"idPlaca\", " +
                        "\"seriePlaca\", \"estado\",\"costo\"," +
                        "\"comision\",\"total\",\"totalConIva\",\"anoRenovacion\",\"idTipoEstatus\",\"idVehiculo\") " +
                        "VALUES (DEFAULT,?,?,?,?,?,?,?,?,?)");
                ps.setString(1, Placa.getSeriePlaca());
                ps.setString(2, Placa.getEstado());
                ps.setBigDecimal(3,Placa.getCosto());
                ps.setBigDecimal(4, Placa.getComision());
                ps.setBigDecimal(5, Placa.getTotal());
                ps.setBigDecimal(6, Placa.getTotalConIva());
                ps.setInt(7, Placa.getAnoRenovacion());
                ps.setInt(8, Placa.getIdTipoEstatus());
                ps.setInt(9, Placa.getIdVehiculo());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                PlacaResult = findById(Placa.getSeriePlaca());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return PlacaResult;
    }

    @Override
    public boolean deleteById(String seriePlaca){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Placas\" where \"seriePlaca\"=?");
            ps.setString(1, seriePlaca);

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
    public Placas findById(int id) {
        Placas placa = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Placas\" where \"idPlaca\" = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                placa = new Placas();
                placa.setIdPlaca(rs.getInt("idPlaca"));
                placa.setSeriePlaca(rs.getString("seriePlaca"));
                placa.setEstado(rs.getString("estado"));
                placa.setCosto(rs.getBigDecimal("costo"));
                placa.setComision(rs.getBigDecimal("comision"));
                placa.setTotal(rs.getBigDecimal("total"));
                placa.setTotalConIva(rs.getBigDecimal("totalConIva"));
                placa.setAnoRenovacion(rs.getInt("anoRenovacion"));
                placa.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                placa.setIdVehiculo(rs.getInt("idVehiculo"));
                System.out.println(placa);
            }
            Conexion.endConexion(conn);
            return placa;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return placa;
    }

    public boolean existByNumeroSerie(int idVehiculo){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Placas\" where \"idVehiculo\" = ?" +
                    "AND idTipoEstatus=1");
            ps.setInt(1,idVehiculo);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Id vehículo asignado a un placa? "+result);
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
