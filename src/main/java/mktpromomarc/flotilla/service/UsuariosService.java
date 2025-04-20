package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Usuarios;
import mktpromomarc.flotilla.repository.UsuariosRepository;
import mktpromomarc.flotilla.security.Encoder;
import org.json.JSONArray;


public class UsuariosService implements ICrudService<Usuarios> {

    private UsuariosRepository usuariosRepository;

    public UsuariosService(UsuariosRepository usuariosRepository){
        this.usuariosRepository=usuariosRepository;
    }

    @Override
    public JSONArray getAll(){
        return usuariosRepository.findAll();
    }
    @Override
    public Usuarios getById(String email){
        return usuariosRepository.findById(email);
    }
    @Override
    public Usuarios add(Usuarios usuario){
        Usuarios usuarioResult = null;
        if(!usuariosRepository.existsById(usuario.getEmail())) {
            usuarioResult = usuariosRepository.save(usuario);
        }
        return usuarioResult;
    }

    @Override
    public Usuarios update(Usuarios usuario){
        Usuarios usuarioRecovered = usuariosRepository.findById(usuario.getIdUsuario());
        if(!usuario.getEmail().equals(usuarioRecovered.getEmail())){
            if(usuariosRepository.existsById(usuario.getEmail())) {
                return null;
            }
        }
        if(!usuario.getContrasena().equals(usuarioRecovered.getContrasena())){
            usuario.setContrasena(new Encoder().encrypt(usuario.getContrasena()));
        }
        return usuariosRepository.save(usuario);
    }
    @Override
    public boolean delete (String email){
        return usuariosRepository.deleteById(email);
    }

    @Override
    public boolean delete(int rfc){return false;}
    @Override
    public Usuarios getById(int idUsuario){return usuariosRepository.findById(idUsuario);}
}
