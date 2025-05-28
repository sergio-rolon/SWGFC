package mktpromomarc.flotilla.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Servicios {
    private int idServicio;
    private int idTipoServicio;
    private int kilometraje;
    private String fechaServicio;
    private BigDecimal costo;
    private BigDecimal comision;
    private BigDecimal total;
    private BigDecimal totalConIva;
    private int idAsignacion;

    public Servicios(){
    }

    public Servicios(BigDecimal costo, BigDecimal comision) {
        this.costo = costo;
        this.comision = comision;
        this.total = costo.add(comision.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(costo));
        this.totalConIva = total.multiply(new BigDecimal("1.16"));
    }

    public Servicios(int idServicio, int idTipoServicio, int kilometraje, String fechaServicio, BigDecimal costo, BigDecimal comision, int idAsignacion) {
        this.idServicio = idServicio;
        this.idTipoServicio = idTipoServicio;
        this.kilometraje = kilometraje;
        this.fechaServicio = fechaServicio;
        this.costo = costo;
        this.comision = comision;
        this.total = costo.add(comision.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(costo));
        this.totalConIva = total.multiply(new BigDecimal("1.16"));
        this.idAsignacion = idAsignacion;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getIdTipoServicio() {
        return idTipoServicio;
    }

    public void setIdTipoServicio(int idTipoServicio) {
        this.idTipoServicio = idTipoServicio;
    }

    public int getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(int kilometraje) {
        this.kilometraje = kilometraje;
    }

    public String getFechaServicio() {
        return fechaServicio;
    }

    public void setFechaServicio(String fechaServicio) {
        this.fechaServicio = fechaServicio;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
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

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    @Override
    public String toString() {
        return "Servicios{" +
                "idServicio=" + idServicio +
                ", idTipoServicio=" + idTipoServicio +
                ", kilometraje=" + kilometraje +
                ", fechaServicio='" + fechaServicio + '\'' +
                ", costo=" + costo +
                ", comision=" + comision +
                ", total=" + total +
                ", totalConIva=" + totalConIva +
                ", idAsignacion=" + idAsignacion +
                '}';
    }
}
