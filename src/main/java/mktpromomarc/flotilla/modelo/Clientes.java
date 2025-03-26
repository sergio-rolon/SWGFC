package mktpromomarc.flotilla.modelo;

public class Clientes {

    private int idCliente;
    private String razonSocial;
    private String rfc;
    private int idEstatus;
    private int idUsuario;

    public Clientes(){
    }

    public Clientes(int idCliente, String razonSocial, String rfc, int idEstatus, int idUsuario) {
        this.idCliente = idCliente;
        this.razonSocial = razonSocial;
        this.rfc = rfc;
        this.idEstatus = idEstatus;
        this.idUsuario = idUsuario;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Clientes{" +
                "idCliente=" + idCliente +
                ", razonSocial='" + razonSocial + '\'' +
                ", rfc='" + rfc + '\'' +
                ", idEstatus=" + idEstatus +
                ", idUsuario=" + idUsuario +
                '}';
    }
}
