package mktpromomarc.flotilla.repository;

import mktpromomarc.flotilla.modelo.Vehiculos;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VehiculosRepository implements ICrudRepository<Vehiculos>{

    @Override
    public JSONArray findAll(){
        return null;
    }

    public JSONArray findAll(boolean isAsesor, String emailAsesor){
        JSONArray allVehiculos = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps;
            if(isAsesor){
                ps = conn.prepareStatement("SELECT v.\"idVehiculo\", v.\"numeroSerie\", " +
                        "v.\"marca\",v.\"tipo\", v.\"modelo\", v.\"accesorios\", v.\"idCliente\", " +
                        "c.\"rfc\", c.\"razonSocial\", " +
                        "te1.\"tipoEstatus\" AS \"estatusVehiculo\", " +
                        "te2.\"tipoEstatus\" AS \"estatusCliente\" " +
                        "FROM \"Vehiculos\" v " +
                        "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\""+
                        "INNER JOIN \"Usuarios\" u ON c.\"idUsuario\" = u.\"idUsuario\"" +
                        "INNER JOIN \"TipoEstatus\" te1 ON v.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON c.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "WHERE u.\"email\" = ? " +
                        "ORDER BY v.\"idVehiculo\" ASC"
                );
                ps.setString(1,emailAsesor);
            }else{
                ps = conn.prepareStatement("SELECT v.\"idVehiculo\", v.\"numeroSerie\", " +
                        "v.\"marca\",v.\"tipo\", v.\"modelo\", v.\"accesorios\", v.\"idCliente\", c.\"rfc\",  " +
                        "c.\"razonSocial\", " +
                        "te1.\"tipoEstatus\" AS \"estatusVehiculo\", " +
                        "te2.\"tipoEstatus\" AS \"estatusCliente\" " +
                        "FROM \"Vehiculos\" v " +
                        "INNER JOIN \"Clientes\" c ON v.\"idCliente\" = c.\"idCliente\""+
                        "INNER JOIN \"TipoEstatus\" te1 ON v.\"idTipoEstatus\" = te1.\"idTipoEstatus\" " +
                        "INNER JOIN \"TipoEstatus\" te2 ON c.\"idTipoEstatus\" = te2.\"idTipoEstatus\" " +
                        "ORDER BY v.\"idVehiculo\" ASC"
                );
            }
            ResultSet rs = ps.executeQuery();
            allVehiculos = new JSONArray();
            while(rs.next()){
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject vehiculo = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    vehiculo.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allVehiculos.put(vehiculo);

            }
            Conexion.endConexion(conn);
            return allVehiculos;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allVehiculos;
    }
    @Override
    public JSONArray findAllObjects() {
        return null;
    }
    public JSONArray findAllObjects(String pathInfo) {
        JSONArray allVehiculos = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps=null;
            if(pathInfo.equals("/getVehiculosSinArrendamiento")) {
                ps = conn.prepareStatement(
                        "SELECT * FROM \"Vehiculos\" v " +
                                "WHERE NOT EXISTS (" +
                                "SELECT 1 FROM \"Arrendamientos\" a WHERE a.\"idVehiculo\" = v.\"idVehiculo\"" +
                                ") AND v.\"idTipoEstatus\"=1 ORDER BY v.\"idVehiculo\" ASC"
                );
            }else if(pathInfo.equals("/getVehiculosActivos")){
                ps = conn.prepareStatement(
                        "SELECT * FROM \"Vehiculos\" v " +
                                "WHERE v.\"idTipoEstatus\"=1 ORDER BY v.\"idVehiculo\" ASC"
                );

//            }
//            else if(pathInfo.equals("/getVehiculosSinPlaca")){
//                ps = conn.prepareStatement(
//                        "SELECT * FROM \"Vehiculos\" v " +
//                                "WHERE NOT EXISTS (" +
//                                "SELECT 1 FROM \"Placas\" a WHERE a.\"idVehiculo\" = v.\"idVehiculo\"" +
//                                "AND a.\"idTipoEstatus\"=1" +
//                                ") AND v.\"idTipoEstatus\"=1 ORDER BY v.\"idVehiculo\" ASC"
//                );

            }else if(pathInfo.equals("/getVehiculosParaAsignacion")){
                ps = conn.prepareStatement("SELECT \"idVehiculo\", \"numeroSerie\", \"idCliente\"" +
                        "FROM \"Vehiculos\" c " +
                        "where \"idTipoEstatus\" = ? "+
                        "ORDER BY c.\"idVehiculo\" ASC"
                );
                ps.setInt(1,1);
            }

            ResultSet rs = ps.executeQuery();
            allVehiculos = new JSONArray();
            while (rs.next()) {
                int totalColumns = rs.getMetaData().getColumnCount();
                JSONObject vehiculo = new JSONObject();
                for(int i=0; i<totalColumns;i++){
                    vehiculo.put(rs.getMetaData().getColumnLabel(i+1),rs.getObject(i+1));
                }
                allVehiculos.put(vehiculo);
            }
            Conexion.endConexion(conn);
        } catch (Exception e) {
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return allVehiculos;
    }
    @Override
    public Vehiculos findById(String numeroSerie){
        Vehiculos vehiculo = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Vehiculos\" where \"numeroSerie\" = ?");
            ps.setString(1,numeroSerie);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                vehiculo = new Vehiculos();
                vehiculo.setIdVehiculo(rs.getInt("idVehiculo"));
                vehiculo.setNumeroSerie(rs.getString("numeroSerie"));
                vehiculo.setMarca(rs.getString("marca"));
                vehiculo.setTipo(rs.getString("tipo"));
                vehiculo.setModelo(rs.getInt("modelo"));
                vehiculo.setAccesorios(rs.getString("accesorios"));
                vehiculo.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                vehiculo.setIdCliente(rs.getInt("idCliente"));
                System.out.println(vehiculo);
            }
            Conexion.endConexion(conn);
            return vehiculo;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return vehiculo;
    }

    @Override
    public boolean existsById(String numeroSerie){
        boolean result = false;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM public.\"Vehiculos\" where \"numeroSerie\" = ?");
            ps.setString(1,numeroSerie);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                result = rs.getBoolean(1);
                System.out.println("Existe el vehículo? "+result);
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
    public Vehiculos save(Vehiculos vehiculo){
        Vehiculos vehiculoResult = null;
        // Validar si vehiculo.idVehiculo es diferente de null entonces es un update
        // si no es un insert
        Connection conn = Conexion.getConexion();
        if(vehiculo.getIdVehiculo()!=0){
            //update
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE public.\"Vehiculos\" SET " +
                        "\"numeroSerie\"=?,\"marca\"=?,\"tipo\"=?,\"modelo\"=?,\"accesorios\"=?,\"idTipoEstatus\"=?," +
                        "\"idCliente\"=? where \"idVehiculo\"=?;");
                ps.setString(1, vehiculo.getNumeroSerie());
                ps.setString(2, vehiculo.getMarca());
                ps.setString(3, vehiculo.getTipo());
                ps.setInt(4, vehiculo.getModelo());
                ps.setString(5, vehiculo.getAccesorios());
                ps.setInt(6, vehiculo.getIdTipoEstatus());
                ps.setInt(7, vehiculo.getIdCliente());
                ps.setInt(8, vehiculo.getIdVehiculo());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                return vehiculo;

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }

        }else {
            //insert
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO public.\"Vehiculos\" (\"idVehiculo\", " +
                        "\"numeroSerie\", \"marca\", \"tipo\",\"modelo\",\"accesorios\",\"idTipoEstatus\", " +
                        "\"idCliente\") VALUES (DEFAULT,?,?,?,?,?,?,?)");
                ps.setString(1, vehiculo.getNumeroSerie());
                ps.setString(2, vehiculo.getMarca());
                ps.setString(3, vehiculo.getTipo());
                ps.setInt(4, vehiculo.getModelo());
                ps.setString(5, vehiculo.getAccesorios());
                ps.setInt(6, vehiculo.getIdTipoEstatus());
                ps.setInt(7, vehiculo.getIdCliente());

                ps.executeUpdate();
                Conexion.endConexion(conn);

                vehiculoResult = findById(vehiculo.getNumeroSerie());

            } catch (Exception e) {
                System.out.println(e);
                Conexion.endConexion(conn);
            }
        }
        return vehiculoResult;
    }

    @Override
    public boolean deleteById(String numeroSerie){
        boolean result=false;
        Connection conn = Conexion.getConexion();
        try {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM public.\"Vehiculos\" where \"numeroSerie\"=?");
            ps.setString(1, numeroSerie);

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
    public Vehiculos findById(int id) {
        Vehiculos vehiculo = null;
        Connection conn = Conexion.getConexion();
        try{
            PreparedStatement ps = conn.prepareStatement("SELECT*FROM public.\"Vehiculos\" where \"idVehiculo\" = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                vehiculo = new Vehiculos();
                vehiculo.setIdVehiculo(rs.getInt("idVehiculo"));
                vehiculo.setNumeroSerie(rs.getString("numeroSerie"));
                vehiculo.setMarca(rs.getString("marca"));
                vehiculo.setModelo(rs.getInt("modelo"));
                vehiculo.setTipo(rs.getString("tipo"));
                vehiculo.setAccesorios(rs.getString("accesorios"));
                vehiculo.setIdTipoEstatus(rs.getInt("idTipoEstatus"));
                vehiculo.setIdCliente(rs.getInt("idCliente"));
                System.out.println(vehiculo);
            }
            Conexion.endConexion(conn);
            return vehiculo;
        }catch (Exception e){
            System.out.println(e);
            Conexion.endConexion(conn);
        }
        return vehiculo;
    }
}
