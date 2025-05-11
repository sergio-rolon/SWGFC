package mktpromomarc.flotilla.modelo;

public class Vehiculos {

    private int idVehiculo;
    private String numeroSerie;
    private String marca;
    private String tipo;
    private int modelo;
    private String accesorios;
    private int idTipoEstatus;
    private int idCliente;

    public Vehiculos(){
    }

    public Vehiculos(int idVehiculo, String numeroSerie, String marca, String tipo, int modelo, String accesorios, int idTipoEstatus, int idCliente) {
        this.idVehiculo = idVehiculo;
        this.numeroSerie = numeroSerie;
        this.marca = marca;
        this.tipo = tipo;
        this.modelo = modelo;
        this.accesorios = accesorios;
        this.idTipoEstatus = idTipoEstatus;
        this.idCliente = idCliente;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getModelo() {
        return modelo;
    }

    public void setModelo(int modelo) {
        this.modelo = modelo;
    }

    public String getAccesorios() {
        return accesorios;
    }

    public void setAccesorios(String accesorios) {
        this.accesorios = accesorios;
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
        return "Vehiculos{" +
                "idVehiculo=" + idVehiculo +
                ", numeroSerie='" + numeroSerie + '\'' +
                ", marca='" + marca + '\'' +
                ", tipo='" + tipo + '\'' +
                ", modelo=" + modelo +
                ", accesorios='" + accesorios + '\'' +
                ", idTipoEstatus=" + idTipoEstatus +
                ", idCliente=" + idCliente +
                '}';
    }
}
