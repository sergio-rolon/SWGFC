package org.example.dto;

public class Credentials {
    String token;
    String email;
    String contrasena;

    public Credentials() {
    }

    public Credentials(String token, String username, String contrasena) {
        this.token = token;
        this.email = username;
        this.contrasena = contrasena;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
