package mktpromomarc.flotilla.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Placas {


    private int idPlaca;
    private String seriePlaca;
    private String estado;
    private BigDecimal costo;
    private BigDecimal comision;
    private BigDecimal total;
    private BigDecimal totalConIva;
    private int anoRenovacion;
    private int idTipoEstatus;
    private int idVehiculo;

    public Placas(){
    }

    public Placas(BigDecimal costo, BigDecimal comision) {
        this.costo = costo;
        this.comision = comision;
        this.total = costo.add(comision.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(costo));
        this.totalConIva = total.multiply(new BigDecimal("1.16"));
    }

    public Placas(int idPlaca, String seriePlaca, String estado,BigDecimal costo, BigDecimal comision, int anoRenovacion, int idTipoEstatus, int idVehiculo) {
        this.idPlaca = idPlaca;
        this.seriePlaca = seriePlaca;
        this.estado = estado;
        this.costo = costo;
        this.comision = comision;
        this.total = costo.add(comision.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP).multiply(costo));
        this.totalConIva = total.multiply(new BigDecimal("1.16"));
        this.anoRenovacion = anoRenovacion;
        this.idTipoEstatus = idTipoEstatus;
        this.idVehiculo = idVehiculo;
    }

    public int getIdPlaca() {
        return idPlaca;
    }

    public void setIdPlaca(int idPlaca) {
        this.idPlaca = idPlaca;
    }

    public String getSeriePlaca() {
        return seriePlaca;
    }

    public void setSeriePlaca(String seriePlaca) {
        this.seriePlaca = seriePlaca;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public int getAnoRenovacion() {
        return anoRenovacion;
    }

    public void setAnoRenovacion(int anoRenovacion) {
        this.anoRenovacion = anoRenovacion;
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
        return "Placas{" +
                "idPlaca=" + idPlaca +
                ", seriePlaca='" + seriePlaca + '\'' +
                ", estado='" + estado + '\'' +
                ", costo=" + costo +
                ", comision=" + comision +
                ", total=" + total +
                ", totalConIva=" + totalConIva +
                ", anoRenovacion=" + anoRenovacion +
                ", idTipoEstatus=" + idTipoEstatus +
                ", idVehiculo=" + idVehiculo +
                '}';
    }
}
