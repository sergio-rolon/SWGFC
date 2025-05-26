package mktpromomarc.flotilla.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Arrendamientos {


    private int idArrendamiento;
    private String numeroContrato;
    private String arrendadora;
    private String fechaInicio;
    private String fechaTermino;
    private BigDecimal mensualidad;
    private BigDecimal comision;
    private BigDecimal total;
    private BigDecimal totalConIva;
    private int numeroMeses;
    private int idTipoEstatus;
    private int idVehiculo;

    public Arrendamientos(){
    }

    public Arrendamientos(BigDecimal mensualidad, BigDecimal comision) {
        this.mensualidad = mensualidad;
        this.comision = comision;
        this.total = mensualidad.add(comision.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(mensualidad));
        this.totalConIva = total.multiply(new BigDecimal("1.16"));
    }

    public Arrendamientos(int idArrendamiento, String numeroContrato, String arrendadora, String fechaInicio, String fechaTermino, BigDecimal mensualidad, BigDecimal comision, int numeroMeses, int idTipoEstatus, int idVehiculo) {
        this.idArrendamiento = idArrendamiento;
        this.numeroContrato = numeroContrato;
        this.arrendadora = arrendadora;
        this.fechaInicio = fechaInicio;
        this.fechaTermino = fechaTermino;
        this.mensualidad = mensualidad;
        this.comision = comision;
        this.total = mensualidad.add(comision.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(mensualidad));
        this.totalConIva = total.multiply(new BigDecimal("1.16"));
        this.numeroMeses = numeroMeses;
        this.idTipoEstatus = idTipoEstatus;
        this.idVehiculo = idVehiculo;
    }

    public int getIdArrendamiento() {
        return idArrendamiento;
    }

    public void setIdArrendamiento(int idArrendamiento) {
        this.idArrendamiento = idArrendamiento;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getArrendadora() {
        return arrendadora;
    }

    public void setArrendadora(String arrendadora) {
        this.arrendadora = arrendadora;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaTermino() {
        return fechaTermino;
    }

    public void setFechaTermino(String fechaTermino) {
        this.fechaTermino = fechaTermino;
    }

    public BigDecimal getMensualidad() {
        return mensualidad;
    }

    public void setMensualidad(BigDecimal mensualidad) {
        this.mensualidad = mensualidad;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotalConIva() {
        return totalConIva;
    }

    public void setTotalConIva(BigDecimal totalConIva) {
        this.totalConIva = totalConIva;
    }

    public int getNumeroMeses() {
        return numeroMeses;
    }

    public void setNumeroMeses(int numeroMeses) {
        this.numeroMeses = numeroMeses;
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

    @Override
    public String toString() {
        return "Arrendamientos{" +
                "idArrendamiento=" + idArrendamiento +
                ", numeroContrato='" + numeroContrato + '\'' +
                ", arrendadora='" + arrendadora + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaTermino='" + fechaTermino + '\'' +
                ", mensualidad=" + mensualidad +
                ", comision=" + comision +
                ", total=" + total +
                ", totalConIva=" + totalConIva +
                ", numeroMeses=" + numeroMeses +
                ", idTipoEstatus=" + idTipoEstatus +
                ", idVehiculo=" + idVehiculo +
                '}';
    }
}
