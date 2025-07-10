package mktpromomarc.flotilla.modelo;

public class Incidentes {
    private int idIncidente;
    private int idTipoIncidente;
    private String descripcion;
    private String fechaIncidente;
    private int idAsignacion;

    public Incidentes(){
    }

    public Incidentes(int idIncidente, int idTipoIncidente, String descripcion, String fechaIncidente, int idAsignacion) {
        this.idIncidente = idIncidente;
        this.idTipoIncidente = idTipoIncidente;
        this.descripcion = descripcion;
        this.fechaIncidente = fechaIncidente;
        this.idAsignacion = idAsignacion;
    }

    public int getIdIncidente() {
        return idIncidente;
    }

    public void setIdIncidente(int idIncidente) {
        this.idIncidente = idIncidente;
    }

    public int getIdTipoIncidente() {
        return idTipoIncidente;
    }

    public void setIdTipoIncidente(int idTipoIncidente) {
        this.idTipoIncidente = idTipoIncidente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaIncidente() {
        return fechaIncidente;
    }

    public void setFechaIncidente(String fechaIncidente) {
        this.fechaIncidente = fechaIncidente;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    @Override
    public String toString() {
        return "Incidentes{" +
                "idIncidente=" + idIncidente +
                ", idTipoIncidente=" + idTipoIncidente +
                ", descripcion='" + descripcion + '\'' +
                ", fechaIncidente='" + fechaIncidente + '\'' +
                ", idAsignacion=" + idAsignacion +
                '}';
    }
}
