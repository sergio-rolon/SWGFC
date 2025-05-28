package mktpromomarc.flotilla.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import mktpromomarc.flotilla.modelo.Servicios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServiciosRepository implements ICrudRepository<Servicios>{

    @Override
    public JSONArray findAll(){
        return null;
    }
    public JSONArray findAll(boolean isAsesor, String emailAsesor){
        JSONArray allServicios = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(isAsesor){
                ps = conn.prepareStatement("SELECT s.\"idServicio\", s.\"idTipoServicio\", s.\"kilometraje\", " +
                        "s.\"fechaServicio\", s.\"costo\", s.\"comision\", s.\"total\", " +
                        "s.\"totalConIva\",s.\"idAsignacion\", "+
                        "v.\"numeroSerie\"," +
                        "te1.\"tipoEstatus\" AS \"estatusServicio\", " +
                        "te2.\"tipoEstatus\" AS \"tipoServicio\" " +
                        "FROM \"Servicios\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idAsignacion\" = v.\"idAsignacion\""+
                        "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                        "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "WHERE u.\"email\" = ? " +
                        "ORDER BY a.\"idServicio\" ASC"
                );
                ps.setString(1,emailAsesor);
            }else{
                ps = conn.prepareStatement("SELECT a.\"idServicio\", a.\"idTipoServicio\", a.\"kilometraje\", " +
                        "a.\"costo\", a.\"comision\", a.\"total\", " +
                        "a.\"totalConIva\",a.\"anoRenovacion\", "+
                        "v.\"numeroSerie\",v.\"idAsignacion\"," +
                        "te1.\"tipoEstatus\" AS \"estatusServicio\", " +
                        "te2.\"tipoEstatus\" AS \"estatusVehiculo\" " +
                        "FROM \"Servicios\" a " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idAsignacion\" = v.\"idAsignacion\""+
                        "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON v.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "ORDER BY a.\"idServicio\" ASC"
                );
            }
            ResultSet rs = ps.executeQuery();
            allServicios = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject servicio = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    servicio.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allServicios.put(servicio);

            }
            Conexion.endConexion(conn);
            return allServicios;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allServicios;
    }

    @Override
    public JSONArray findAllObjects() {
        return null;
    }

    @Override
    public Servicios findById(String idTipoServicio){
        Servicios servicio = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Servicios\" where \"idTipoServicio\" = ?");
            ps.setString(1,idTipoServicio);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                servicio = new Servicios();
                servicio.setIdServicio(rs.getInt("idServicio"));
                servicio.setIdTipoServicio(rs.getInt("idTipoServicio"));
                servicio.setKilometraje(rs.getInt("kilometraje"));
                servicio.setCosto(rs.getBigDecimal("costo"));
                servicio.setComision(rs.getBigDecimal("comision"));
                servicio.setTotal(rs.getBigDecimal("total"));
                servicio.setTotalConIva(rs.getBigDecimal("totalConIva"));
                servicio.setIdAsignacion(rs.getInt("idAsignacion"));
                System.out.println(servicio);
            }
            Conexion.endConexion(conn);
            return servicio;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return servicio;
    }

    @Override
    public boolean existsById(String idTipoServicio){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Servicios\" where \"idTipoServicio\" = ?");
            ps.setString(1,idTipoServicio);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el servicio? "+result);
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
    public Servicios save(Servicios Servicio){
        Servicios ServicioResult = null;
        // Validar si Servicio.idServicio es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(Servicio.getIdServicio()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Servicios\" SET " +
                        "\"idTipoServicio\"=?,\"kilometraje\"=?,\"costo\"=?," +
                        "\"comision\"=?,\"total\"=?,\"totalConIva\"=?,\"anoRenovacion\"=?,\"idTipoEstatus\"=?," +
                        "\"idAsignacion\"=? where \"idServicio\"=?;");
                ps.setInt(1, Servicio.getIdTipoServicio());
                ps.setInt(2, Servicio.getKilometraje());
                ps.setBigDecimal(3,Servicio.getCosto());
                ps.setBigDecimal(4, Servicio.getComision());
                ps.setBigDecimal(5, Servicio.getTotal());
                ps.setBigDecimal(6, Servicio.getTotalConIva());
                ps.setInt(7, Servicio.getIdAsignacion());
                ps.setInt(8, Servicio.getIdServicio());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return Servicio;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Servicios\" (\"idServicio\", " +
                        "\"idTipoServicio\", \"kilometraje\",\"costo\"," +
                        "\"comision\",\"total\",\"totalConIva\",\"anoRenovacion\",\"idTipoEstatus\",\"idAsignacion\") " +
                        "VALUES (DEFAULT,?,?,?,?,?,?,?,?,?)");
                ps.setInt(1, Servicio.getIdTipoServicio());
                ps.setInt(2, Servicio.getKilometraje());
                ps.setBigDecimal(3,Servicio.getCosto());
                ps.setBigDecimal(4, Servicio.getComision());
                ps.setBigDecimal(5, Servicio.getTotal());
                ps.setBigDecimal(6, Servicio.getTotalConIva());
                ps.setInt(7, Servicio.getIdAsignacion());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                ServicioResult = findById(Servicio.getIdTipoServicio());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return ServicioResult;
    }

    @Override
    public boolean deleteById(String idTipoServicio){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Servicios\" where \"idTipoServicio\"=?");
            ps.setString(1, idTipoServicio);

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
    public Servicios findById(int id) {
        Servicios servicio = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Servicios\" where \"idServicio\" = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                servicio = new Servicios();
                servicio.setIdServicio(rs.getInt("idServicio"));
                servicio.setIdTipoServicio(rs.getInt("idTipoServicio"));
                servicio.setKilometraje(rs.getInt("kilometraje"));
                servicio.setCosto(rs.getBigDecimal("costo"));
                servicio.setComision(rs.getBigDecimal("comision"));
                servicio.setTotal(rs.getBigDecimal("total"));
                servicio.setTotalConIva(rs.getBigDecimal("totalConIva"));
                servicio.setIdAsignacion(rs.getInt("idAsignacion"));
                System.out.println(servicio);
            }
            Conexion.endConexion(conn);
            return servicio;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return servicio;
    }

    public boolean existByNumeroSerie(int idAsignacion){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Servicios\" where \"idAsignacion\" = ?" +
                    "AND idTipoEstatus=1");
            ps.setInt(1,idAsignacion);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Id vehículo asignado a un servicio? "+result);
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
