package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Usuarios;
import mktpromomarc.flotilla.repository.UsuariosRepository;
import org.json.JSONArray;


public class UsuariosService implements CrudService<Usuarios>{

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
        Usuarios usuarioResult = null;
        if(usuariosRepository.existsById(email)) {
            return usuarioResult = usuariosRepository.findById(email);
        }
        return usuarioResult;
    }
    @Override
    public Usuarios add(Usuarios usuario){
        Usuarios usuarioResult = null;
        if(!usuariosRepository.existsById(usuario.getEmail())) {
            return usuarioResult = usuariosRepository.save(usuario);
        }
        return usuarioResult;
    }

    @Override
    public Usuarios update(Usuarios usuario){
        Usuarios usuarioResult = null;
        if(usuariosRepository.existsById(usuario.getEmail())) {
            usuario.setIdUsuario(usuariosRepository.findById(usuario.getEmail()).getIdUsuario());
            return usuarioResult = usuariosRepository.save(usuario);
        }
        return usuarioResult;
    }
    @Override
    public boolean delete (String email){
        boolean result = false;
        if(usuariosRepository.existsById(email)) {
            usuariosRepository.deleteById(email);
            result = true;
        }
        return result;
    }

    @Override
    public boolean delete(int rfc){return false;}
    @Override
    public Usuarios getById(int rfc){return new Usuarios();}
}
