package org.example.service;

import org.example.modelo.Usuarios;
import org.example.repository.UsuariosRepository;

import java.util.List;

public class UsuariosService {

    public static List<Usuarios> getAllUsuarios(){
        return UsuariosRepository.findAll();
    }

    public static Usuarios getUsuarioById(String email){
        Usuarios usuarioResult = null;
        if(UsuariosRepository.existsById(email)) {
            return usuarioResult = UsuariosRepository.findById(email);
        }
        return usuarioResult;
    }

    public static Usuarios addUsuario(Usuarios usuario){
        Usuarios usuarioResult = null;
        if(!UsuariosRepository.existsById(usuario.getEmail())) {
            return usuarioResult = UsuariosRepository.save(usuario);
        }
        return usuarioResult;
    }


    public static Usuarios updateUsuario(Usuarios usuario){
        Usuarios usuarioResult = null;
        if(UsuariosRepository.existsById(usuario.getEmail())) {
            return usuarioResult = UsuariosRepository.save(usuario);
        }
        return usuarioResult;
    }

    public static boolean deleteUsuario (String email){
        boolean result = false;
        if(UsuariosRepository.existsById(email)) {
            UsuariosRepository.deleteById(email);
            result = true;
        }
        return result;
    }
}
