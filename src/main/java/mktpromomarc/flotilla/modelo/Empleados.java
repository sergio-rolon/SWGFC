package mktpromomarc.flotilla.modelo;

public class Empleados {

    private int idEmpleado;
    private String numeroTrabajador;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String municipioAsignado;
    private String estadoAsignado;
    private int cantidadGasolina;
    private int idTipoEstatus;
    private int idCliente;

    public Empleados(){
    }

    public Empleados(int idEmpleado, String numeroTrabajador, String nombre, String apellidoPaterno, String apellidoMaterno, String municipioAsignado, String estadoAsignado, int cantidadGasolina, int idTipoEstatus, int idCliente) {
        this.idEmpleado = idEmpleado;
        this.numeroTrabajador = numeroTrabajador;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.municipioAsignado = municipioAsignado;
        this.estadoAsignado = estadoAsignado;
        this.cantidadGasolina = cantidadGasolina;
        this.idTipoEstatus = idTipoEstatus;
        this.idCliente = idCliente;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getNumeroTrabajador() {
        return numeroTrabajador;
    }

    public void setNumeroTrabajador(String numeroTrabajador) {
        this.numeroTrabajador = numeroTrabajador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getMunicipioAsignado() {
        return municipioAsignado;
    }

    public void setMunicipioAsignado(String municipioAsignado) {
        this.municipioAsignado = municipioAsignado;
    }

    public String getEstadoAsignado() {
        return estadoAsignado;
    }

    public void setEstadoAsignado(String estadoAsignado) {
        this.estadoAsignado = estadoAsignado;
    }

    public int getCantidadGasolina() {
        return cantidadGasolina;
    }

    public void setCantidadGasolina(int cantidadGasolina) {
        this.cantidadGasolina = cantidadGasolina;
    }

    public int getIdTipoEstatus() {
        return idTipoEstatus;
    }

    public void setIdTipoEstatus(int idTipoEstatus) {
        this.idTipoEstatus = idTipoEstatus;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    @Override
    public String toString() {
        return "Empleados{" +
                "idEmpleado=" + idEmpleado +
                ", numeroTrabajador='" + numeroTrabajador + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", municipioAsignado='" + municipioAsignado + '\'' +
                ", estadoAsignado='" + estadoAsignado + '\'' +
                ", cantidadGasolina='" + cantidadGasolina + '\'' +
                ", idTipoEstatus=" + idTipoEstatus +
                ", idCliente=" + idCliente +
                '}';
    }
}
