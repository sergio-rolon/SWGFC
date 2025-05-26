package mktpromomarc.flotilla.modelo;

public class Asignaciones {

    private int idAsignacion;
    private int idTipoEstatus;
    private int idVehiculo;
    private int idEmpleado;

    public Asignaciones(){
    }

    public Asignaciones(int idAsignacion, int idTipoEstatus, int idVehiculo, int idEmpleado) {
        this.idAsignacion = idAsignacion;
        this.idTipoEstatus = idTipoEstatus;
        this.idVehiculo = idVehiculo;
        this.idEmpleado = idEmpleado;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public int getIdTipoEstatus() {
        return idTipoEstatus;
    }

    public void setIdTipoEstatus(int idTipoEstatus) {
        this.idTipoEstatus = idTipoEstatus;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    @Override
    public String toString() {
        return "Asignaciones{" +
                "idAsignacion=" + idAsignacion +
                ", idTipoEstatus=" + idTipoEstatus +
                ", idVehiculo=" + idVehiculo +
                ", idEmpleado=" + idEmpleado +
                '}';
    }
}
