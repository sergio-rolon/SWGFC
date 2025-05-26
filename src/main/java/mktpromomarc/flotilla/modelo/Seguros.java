package mktpromomarc.flotilla.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Seguros {


    private int idSeguro;
    private String numeroPoliza;
    private String aseguradora;
    private String fechaInicio;
    private String fechaTermino;
    private BigDecimal mensualidad;
    private BigDecimal comision;
    private BigDecimal total;
    private BigDecimal totalConIva;
    private int numeroMeses;
    private int idTipoEstatus;
    private int idVehiculo;

    public Seguros(){
    }

    public Seguros(BigDecimal mensualidad, BigDecimal comision) {
        this.mensualidad = mensualidad;
        this.comision = comision;
        this.total = mensualidad.add(comision.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(mensualidad));
        this.totalConIva = total.multiply(new BigDecimal("1.16"));
    }

    public Seguros(int idSeguro, String numeroPoliza, String aseguradora, String fechaInicio, String fechaTermino, BigDecimal mensualidad, BigDecimal comision, int numeroMeses, int idTipoEstatus, int idVehiculo) {
        this.idSeguro = idSeguro;
        this.numeroPoliza = numeroPoliza;
        this.aseguradora = aseguradora;
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

    public int getIdSeguro() {
        return idSeguro;
    }

    public void setIdSeguro(int idSeguro) {
        this.idSeguro = idSeguro;
    }

    public String getNumeroPoliza() {
        return numeroPoliza;
    }

    public void setNumeroPoliza(String numeroPoliza) {
        this.numeroPoliza = numeroPoliza;
    }

    public String getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(String aseguradora) {
        this.aseguradora = aseguradora;
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
        return "Seguros{" +
                "idSeguro=" + idSeguro +
                ", numeroPoliza='" + numeroPoliza + '\'' +
                ", aseguradora='" + aseguradora + '\'' +
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
