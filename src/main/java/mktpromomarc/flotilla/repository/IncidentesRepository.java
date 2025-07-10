package mktpromomarc.flotilla.repository;

import org.json.JSONArray;
import org.json.JSONObject;
import mktpromomarc.flotilla.modelo.Incidentes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class IncidentesRepository implements ICrudRepository<Incidentes>{

    @Override
    public JSONArray findAll(){
        return null;
    }
    public JSONArray findAll(boolean isAsesor, String emailAsesor){
        JSONArray allIncidentes = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(isAsesor){
                ps = conn.prepareStatement(
                        "SELECT i.\"idIncidente\", i.\"idTipoIncidente\", i.\"descripcion\", i.\"fechaIncidente\", i.\"idAsignacion\", " +
                                "v.\"numeroSerie\", v.\"marca\", v.\"tipo\", v.\"modelo\", " +
                                "e.\"numeroTrabajador\", e.\"nombre\", e.\"apellidoPaterno\", e.\"apellidoMaterno\", " +
                                "c.\"razonSocial\", c.\"idCliente\", ti.\"tipoIncidente\" AS \"tipoIncidente\", " +
                                "te1.\"tipoEstatus\" AS \"estatusVehiculo\", te2.\"tipoEstatus\" AS \"estatusTrabajador\"  "+
                                "FROM \"Incidentes\" i " +
                                "INNER JOIN \"Asignaciones\" a ON i.\"idAsignacion\" = a.\"idAsignacion\" " +
                                "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\" " +
                                "INNER JOIN \"Empleados\" e ON a.\"idEmpleado\" = e.\"idEmpleado\" " +
                                "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                                "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                                "INNER JOIN \"TipoIncidente\" ti ON i.\"idTipoIncidente\" = ti.\"idTipoIncidente\" " +
                                "INNER JOIN \"TipoEstatus\" te1 ON v.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                                "INNER JOIN \"TipoEstatus\" te2 ON e.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                                "WHERE u.\"email\" = ? " +
                                "ORDER BY i.\"idIncidente\" ASC"
                );
                ps.setString(1,emailAsesor);
            }else{
                ps = conn.prepareStatement(
                        "SELECT i.\"idIncidente\", i.\"idTipoIncidente\", i.\"descripcion\", i.\"fechaIncidente\", i.\"idAsignacion\", " +
                                "v.\"numeroSerie\", v.\"marca\", v.\"tipo\", v.\"modelo\", " +
                                "e.\"numeroTrabajador\", e.\"nombre\", e.\"apellidoPaterno\", e.\"apellidoMaterno\", " +
                                "c.\"razonSocial\", c.\"idCliente\", ti.\"tipoIncidente\" AS \"tipoIncidente\", " +
                                "te1.\"tipoEstatus\" AS \"estatusVehiculo\", te2.\"tipoEstatus\" AS \"estatusTrabajador\"  "+
                                "FROM \"Incidentes\" i " +
                                "INNER JOIN \"Asignaciones\" a ON i.\"idAsignacion\" = a.\"idAsignacion\" " +
                                "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\" " +
                                "INNER JOIN \"Empleados\" e ON a.\"idEmpleado\" = e.\"idEmpleado\" " +
                                "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                                "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                                "INNER JOIN \"TipoIncidente\" ti ON i.\"idTipoIncidente\" = ti.\"idTipoIncidente\" " +
                                "INNER JOIN \"TipoEstatus\" te1 ON v.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                                "INNER JOIN \"TipoEstatus\" te2 ON e.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                                "ORDER BY i.\"idIncidente\" ASC"
                );
            }
            ResultSet rs = ps.executeQuery();
            allIncidentes = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject incidente = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    incidente.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allIncidentes.put(incidente);

            }
            Conexion.endConexion(conn);
            return allIncidentes;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allIncidentes;
    }

    @Override
    public JSONArray findAllObjects() {
        return null;
    }

    @Override
    public Incidentes findById(String idIncidente){
        Incidentes incidente = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Incidentes\" where \"idIncidente\" = ?");
            ps.setString(1,idIncidente);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                incidente = new Incidentes();
                incidente.setIdIncidente(rs.getInt("idIncidente"));
                incidente.setIdTipoIncidente(rs.getInt("idTipoIncidente"));
                incidente.setDescripcion(rs.getString("descripcion"));
                incidente.setFechaIncidente(rs.getString("fechaIncidente"));
                incidente.setIdAsignacion(rs.getInt("idAsignacion"));
                System.out.println(incidente);
            }
            Conexion.endConexion(conn);
            return incidente;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return incidente;
    }

    @Override
    public boolean existsById(String idIncidente){
        return false;
    }
    public boolean existsById(int idIncidente){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Incidentes\" where \"idIncidente\" = ?");
            ps.setInt(1,idIncidente);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el incidente? "+result);
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
    public Incidentes save(Incidentes incidente){
        Incidentes incidenteResult = null;
        // Validar si Incidente.idIncidente es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(incidente.getIdIncidente()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Incidentes\" SET " +
                        "\"idTipoIncidente\"=?,\"descripcion\"=?,\"fechaIncidente\"=?," +
                        "\"idAsignacion\"=? where \"idIncidente\"=?;");
                ps.setInt(1, incidente.getIdTipoIncidente());
                ps.setString(2, incidente.getDescripcion());
                ps.setString(3, incidente.getFechaIncidente());
                ps.setInt(4, incidente.getIdAsignacion());
                ps.setInt(5, incidente.getIdIncidente());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return incidente;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Incidentes\" (\"idIncidente\", " +
                        "\"idTipoIncidente\", \"descripcion\",\"fechaIncidente\"," +
                        "\"idAsignacion\") " +
                        "VALUES (DEFAULT,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, incidente.getIdTipoIncidente());
                ps.setString(2, incidente.getDescripcion());
                ps.setString(3, incidente.getFechaIncidente());
                ps.setInt(4, incidente.getIdAsignacion());

                int rowsInserted = ps.executeUpdate();

                int idGenerated = -1;

                if(rowsInserted>0){
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        idGenerated = rs.getInt(1);
                    }
                    rs.close();
                }
                incidente.setIdIncidente(idGenerated);
                Conexion.endConexion(conn);

                incidenteResult = incidente;
            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return incidenteResult;
    }

    @Override
    public boolean deleteById(String idTipoIncidente){
        return false;
    }

    @Override
    public boolean deleteById(int idIncidente){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Incidentes\" where \"idIncidente\"=?");
            ps.setInt(1, idIncidente);

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
    public Incidentes findById(int id) {
        Incidentes incidente = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Incidentes\" where \"idIncidente\" = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                incidente = new Incidentes();
                incidente.setIdIncidente(rs.getInt("idIncidente"));
                incidente.setIdTipoIncidente(rs.getInt("idTipoIncidente"));
                incidente.setDescripcion(rs.getString("descripcion"));
                incidente.setDescripcion(rs.getString("fechaIncidente"));
                incidente.setIdAsignacion(rs.getInt("idAsignacion"));
                System.out.println(incidente);
            }
            Conexion.endConexion(conn);
            return incidente;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return incidente;
    }
}
