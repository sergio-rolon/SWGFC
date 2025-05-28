package mktpromomarc.flotilla.repository;


import org.json.JSONArray;
import org.json.JSONObject;
import mktpromomarc.flotilla.modelo.Asignaciones;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AsignacionesRepository implements ICrudRepository<Asignaciones>{

    @Override
    public JSONArray findAll(){
        return null;
    }
    public JSONArray findAll(boolean isAsesor, String emailAsesor){
        JSONArray allAsignaciones = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(isAsesor){
                ps = conn.prepareStatement(
                        "SELECT a.\"idAsignacion\", a.\"idVehiculo\", a.\"idEmpleado\", v.\"numeroSerie\", v.\"marca\", v.\"tipo\", v.\"modelo\", " +
                                "e.\"numeroTrabajador\", e.\"nombre\", e.\"apellidoPaterno\", e.\"apellidoMaterno\", " +
                                "c.\"razonSocial\", c.\"idCliente\"," +
                                "te1.\"tipoEstatus\" AS \"estatusAsignacion\" " +
                                "FROM \"Asignaciones\" a " +
                                "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\" " +
                                "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                                "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                                "INNER JOIN \"Empleados\" e ON a.\"idEmpleado\" = e.\"idEmpleado\" " +
                                "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                                "WHERE u.\"email\" = ? " +
                                "ORDER BY a.\"idAsignacion\" ASC"
                );
                ps.setString(1,emailAsesor);
            }else{
                ps = conn.prepareStatement(
                        "SELECT a.\"idAsignacion\", a.\"idVehiculo\", a.\"idEmpleado\", v.\"numeroSerie\", v.\"marca\", v.\"tipo\", v.\"modelo\", " +
                                "e.\"numeroTrabajador\", e.\"nombre\", e.\"apellidoPaterno\", e.\"apellidoMaterno\", " +
                                "c.\"razonSocial\", c.\"idCliente\", " +
                                "te1.\"tipoEstatus\" AS \"estatusAsignacion\" " +
                                "FROM \"Asignaciones\" a " +
                                "INNER JOIN \"Vehiculos\" v ON a.\"idVehiculo\" = v.\"idVehiculo\" " +
                                "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\" " +
                                "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\" " +
                                "INNER JOIN \"Empleados\" e ON a.\"idEmpleado\" = e.\"idEmpleado\" " +
                                "INNER JOIN \"TipoEstatus\" te1 ON a.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                                "ORDER BY a.\"idAsignacion\" ASC"
                );
            }
            ResultSet rs = ps.executeQuery();
            allAsignaciones = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject asignacion = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    asignacion.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allAsignaciones.put(asignacion);

            }
            Conexion.endConexion(conn);
            return allAsignaciones;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allAsignaciones;
    }

    @Override
    public JSONArray findAllObjects() {
        return null;
    }

    @Override
    public Asignaciones findById(String idAsignacion){
        return null;
    }

    @Override
    public boolean existsById(String numeroPoliza){
        return false;
    }
        public boolean existsById(int idAsignacion){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Asignaciones\" where \"idAsignacion\" = ?");
            ps.setInt(1,idAsignacion);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Id asignación ya se encuentra asignado? "+result);
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
    public Asignaciones save(Asignaciones asignacion){
        Asignaciones asignacionResult = null;
        // Validar si Asignacion.idAsignacion es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(asignacion.getIdAsignacion()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Asignaciones\" SET " +
                        "\"idTipoEstatus\"=?,\"idVehiculo\"=?,\"idEmpleado\"=?" +
                        " where \"idAsignacion\"=?;");
                ps.setInt(1, asignacion.getIdTipoEstatus());
                ps.setInt(2, asignacion.getIdVehiculo());
                ps.setInt(3, asignacion.getIdEmpleado());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return asignacion;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Asignaciones\" (\"idAsignacion\", " +
                        "\"idTipoEstatus\", \"idVehiculo\", \"idEmpleado\") " +
                        "VALUES (DEFAULT,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, asignacion.getIdTipoEstatus());
                ps.setInt(2, asignacion.getIdVehiculo());
                ps.setInt(3, asignacion.getIdEmpleado());

                int rowsInserted = ps.executeUpdate();

                int idGenerated = -1;

                if(rowsInserted>0){
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        idGenerated = rs.getInt(1);
                    }
                    rs.close();
                }
                asignacion.setIdAsignacion(idGenerated);
                Conexion.endConexion(conn);

                asignacionResult = asignacion;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return asignacionResult;
    }
    public Asignaciones save(Asignaciones asignacion, int idCliente){
        Asignaciones asignacionResult = null;
        // Validar si Asignacion.idAsignacion es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement psValidar = conn.prepareStatement(
                    "SELECT COUNT(*) FROM \"Vehiculos\" v, \"Empleados\" e " +
                            "WHERE v.\"idVehiculo\" = ? AND e.\"idEmpleado\" = ? " +
                            "AND v.\"idCliente\" = ? AND e.\"idCliente\" = ?"
            );
            psValidar.setInt(1, asignacion.getIdVehiculo());
            psValidar.setInt(2, asignacion.getIdEmpleado());
            psValidar.setInt(3, idCliente);
            psValidar.setInt(4, idCliente);

            ResultSet rs1 = psValidar.executeQuery();
            if (rs1.next() && rs1.getInt(1) == 0) {

                Conexion.endConexion(conn);
                asignacionResult = new Asignaciones();
                asignacionResult.setIdAsignacion(Integer.parseInt("-2"));
                return asignacionResult;
            }

        if(asignacion.getIdAsignacion()!=0){
            //update

                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Asignaciones\" SET " +
                        "\"idTipoEstatus\"=?,\"idVehiculo\"=?,\"idEmpleado\"=?" +
                        " where \"idAsignacion\"=?;");
                ps.setInt(1, asignacion.getIdTipoEstatus());
                ps.setInt(2, asignacion.getIdVehiculo());
                ps.setInt(3, asignacion.getIdEmpleado());
                ps.setInt(4, asignacion.getIdAsignacion());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return asignacion;
        }else {
            //insert

                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Asignaciones\" (\"idAsignacion\", " +
                        "\"idTipoEstatus\", \"idVehiculo\", \"idEmpleado\") " +
                        "VALUES (DEFAULT,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, asignacion.getIdTipoEstatus());
                ps.setInt(2, asignacion.getIdVehiculo());
                ps.setInt(3, asignacion.getIdEmpleado());

                int rowsInserted = ps.executeUpdate();

                int idGenerated = -1;

                if(rowsInserted>0){
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        idGenerated = rs.getInt(1);
                    }
                    rs.close();
                }
                asignacion.setIdAsignacion(idGenerated);
                Conexion.endConexion(conn);

                asignacionResult = asignacion;


        }
        } catch (Exception e) {
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return asignacionResult;
    }
    @Override
    public boolean deleteById(String numeroPoliza){
        return false;
    }

    @Override
    public boolean deleteById(int idAsignacion){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Asignaciones\" where \"idAsignacion\"=?");
            ps.setInt(1, idAsignacion);

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
    public Asignaciones findById(int idAsignacion) {
        Asignaciones asignacion = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Asignaciones\" where \"idAsignacion\" = ?");
            ps.setInt(1,idAsignacion);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                asignacion = new Asignaciones();
                asignacion.setIdAsignacion(rs.getInt("idAsignacion"));
                asignacion.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                asignacion.setIdVehiculo(rs.getInt("idVehiculo"));
                asignacion.setIdEmpleado(rs.getInt("idEmpleado"));
                System.out.println(asignacion);
            }
            Conexion.endConexion(conn);
            return asignacion;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return asignacion;
    }


}

