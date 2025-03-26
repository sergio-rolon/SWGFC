package mktpromomarc.flotilla.modelo;

public class Usuarios {

    private int idUsuario;
    private String email;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String numeroTrabajador;
    private String contrasena;
    private int idEstatus;
    private int idTipoUsuario;

    public Usuarios() {
    }

    public Usuarios(int idUsuario, String email, String nombre, String apellidoPaterno, String apellidoMaterno, String numeroTrabajador, String contrasena, int idEstatus, int idTipoUsuario) {
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.numeroTrabajador = numeroTrabajador;
        this.contrasena = contrasena;
        this.idEstatus = idEstatus;
        this.idTipoUsuario = idTipoUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getNumeroTrabajador() {
        return numeroTrabajador;
    }

    public void setNumeroTrabajador(String numeroTrabajador) {
        this.numeroTrabajador = numeroTrabajador;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getIdEstatus() {
        return idEstatus;
    }

    public void setIdEstatus(int idEstatus) {
        this.idEstatus = idEstatus;
    }

    public int getIdTipoUsuario() {
        return idTipoUsuario;
    }

    public void setIdTipoUsuario(int idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }

    @Override
    public String toString() {
        return "Usuarios{" +
                "idUsuario=" + idUsuario +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                ", numeroTrabajador='" + numeroTrabajador + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", idEstatus=" + idEstatus +
                ", idTipoUsuario=" + idTipoUsuario +
                '}';
    }
}
