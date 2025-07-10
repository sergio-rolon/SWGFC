package mktpromomarc.flotilla.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import mktpromomarc.flotilla.modelo.Servicios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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
                ps = conn.prepareStatement(
                        "SELECT s.\"idServicio\", s.\"kilometraje\", s.\"idTipoServicio\", s.\"fechaServicio\", " +
                                "s.\"costo\", s.\"comision\", s.\"total\", s.\"totalConIva\", s.\"idAsignacion\", " +
                                "v.\"numeroSerie\", v.\"marca\", v.\"tipo\", v.\"modelo\", " +
                                "e.\"numeroTrabajador\", e.\"nombre\", e.\"apellidoPaterno\", e.\"apellidoMaterno\", " +
                                "c.\"razonSocial\", c.\"idCliente\", ts.\"tipoServicio\" AS \"tipoServicio\", " +
                                "te1.\"tipoEstatus\" AS \"estatusVehiculo\", te2.\"tipoEstatus\" AS \"estatusTrabajador\"  "+
                                "FROM \"Servicios\" s " +
                                "INNER JOIN \"Asignaciones\" a ON s.\"idAsignacion\" = a.\"idAsignacion\" " +
                                "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\" " +
                                "INNER JOIN \"Empleados\" e ON a.\"idEmpleado\" = e.\"idEmpleado\" " +
                                "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                                "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                                "INNER JOIN \"TipoServicio\" ts ON s.\"idTipoServicio\" = ts.\"idTipoServicio\" " +
                                "INNER JOIN \"TipoEstatus\" te1 ON v.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                                "INNER JOIN \"TipoEstatus\" te2 ON e.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                                "WHERE u.\"email\" = ? " +
                                "ORDER BY s.\"idServicio\" ASC"
                );
                ps.setString(1,emailAsesor);
            }else{
                ps = conn.prepareStatement("SELECT s.\"idServicio\", s.\"idTipoServicio\", s.\"kilometraje\", s.\"fechaServicio\", " +
                        "s.\"costo\", s.\"comision\", s.\"total\", s.\"totalConIva\", s.\"idAsignacion\", " +
                        "v.\"numeroSerie\", v.\"marca\", v.\"tipo\", v.\"modelo\", " +
                        "e.\"numeroTrabajador\", e.\"nombre\", e.\"apellidoPaterno\", e.\"apellidoMaterno\", " +
                        "c.\"razonSocial\", c.\"idCliente\", ts.\"tipoServicio\" AS \"tipoServicio\", " +
                        "te1.\"tipoEstatus\" AS \"estatusVehiculo\", te2.\"tipoEstatus\" AS \"estatusTrabajador\"  "+
                        "FROM \"Servicios\" s " +
                        "INNER JOIN \"Asignaciones\" a ON s.\"idAsignacion\" = a.\"idAsignacion\" " +
                        "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\" " +
                        "INNER JOIN \"Empleados\" e ON a.\"idEmpleado\" = e.\"idEmpleado\" " +
                        "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                        "INNER JOIN \"TipoServicio\" ts ON s.\"idTipoServicio\" = ts.\"idTipoServicio\" " +
                        "INNER JOIN \"TipoEstatus\" te1 ON v.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON e.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "ORDER BY s.\"idServicio\" ASC"
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
    public Servicios findById(String idServicio){
        Servicios servicio = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Servicios\" where \"idServicio\" = ?");
            ps.setString(1,idServicio);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                servicio = new Servicios();
                servicio.setIdServicio(rs.getInt("idServicio"));
                servicio.setIdTipoServicio(rs.getInt("idTipoServicio"));
                servicio.setKilometraje(rs.getInt("kilometraje"));
                servicio.setFechaServicio(rs.getString("fechaServicio"));
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
    public boolean existsById(String idServicio){
        return false;
    }
    public boolean existsById(int idServicio){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Servicios\" where \"idServicio\" = ?");
            ps.setInt(1,idServicio);
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
    public Servicios save(Servicios servicio){
        Servicios servicioResult = null;
        // Validar si Servicio.idServicio es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(servicio.getIdServicio()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Servicios\" SET " +
                        "\"idTipoServicio\"=?,\"kilometraje\"=?,\"fechaServicio\"=?,\"costo\"=?," +
                        "\"comision\"=?,\"total\"=?,\"totalConIva\"=?," +
                        "\"idAsignacion\"=? where \"idServicio\"=?;");
                ps.setInt(1, servicio.getIdTipoServicio());
                ps.setInt(2, servicio.getKilometraje());
                ps.setString(3, servicio.getFechaServicio());
                ps.setBigDecimal(4,servicio.getCosto());
                ps.setBigDecimal(5, servicio.getComision());
                ps.setBigDecimal(6, servicio.getTotal());
                ps.setBigDecimal(7, servicio.getTotalConIva());
                ps.setInt(8, servicio.getIdAsignacion());
                ps.setInt(9, servicio.getIdServicio());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return servicio;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Servicios\" (\"idServicio\", " +
                        "\"idTipoServicio\", \"kilometraje\",\"fechaServicio\",\"costo\"," +
                        "\"comision\",\"total\",\"totalConIva\",\"idAsignacion\") " +
                        "VALUES (DEFAULT,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, servicio.getIdTipoServicio());
                    ps.setInt(2, servicio.getKilometraje());
                    ps.setString(3, servicio.getFechaServicio());
                    ps.setBigDecimal(4,servicio.getCosto());
                    ps.setBigDecimal(5, servicio.getComision());
                    ps.setBigDecimal(6, servicio.getTotal());
                    ps.setBigDecimal(7, servicio.getTotalConIva());
                    ps.setInt(8, servicio.getIdAsignacion());

                int rowsInserted = ps.executeUpdate();

                int idGenerated = -1;

                if(rowsInserted>0){
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        idGenerated = rs.getInt(1);
                    }
                    rs.close();
                }
               servicio.setIdServicio(idGenerated);
                Conexion.endConexion(conn);

                servicioResult = servicio;
            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return servicioResult;
    }

    @Override
    public boolean deleteById(String idTipoServicio){
        return false;
    }

    @Override
    public boolean deleteById(int idServicio){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Servicios\" where \"idServicio\"=?");
            ps.setInt(1, idServicio);

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
                servicio.setFechaServicio(rs.getString("fechaServicio"));
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
}
