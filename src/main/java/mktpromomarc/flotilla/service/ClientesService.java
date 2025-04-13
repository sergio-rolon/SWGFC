package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Clientes;
import mktpromomarc.flotilla.modelo.Usuarios;
import mktpromomarc.flotilla.repository.ClientesRepository;
import mktpromomarc.flotilla.security.Encoder;
import org.json.JSONArray;


public class ClientesService implements CrudService<Clientes> {

    private ClientesRepository clientesRepository;

    public ClientesService(ClientesRepository clientesRepository){
        this.clientesRepository=clientesRepository;
    }

    @Override
    public JSONArray getAll(){
        return clientesRepository.findAll();
    }
    @Override
    public Clientes getById(String rfc){
        return clientesRepository.findById(rfc);
    }
    @Override
    public Clientes add(Clientes cliente){
        Clientes clienteResult = null;
        if(!clientesRepository.existsById(cliente.getRfc())) {
            clienteResult = clientesRepository.save(cliente);
        }
        return clienteResult;
    }

    @Override
    public Clientes update(Clientes cliente){
        Clientes clienteRecovered = clientesRepository.findById(cliente.getIdCliente());
        if(!cliente.getRfc().equals(clienteRecovered.getRfc())){
            if(clientesRepository.existsById(cliente.getRfc())) {
                return null;
            }
        }
        return clientesRepository.save(cliente);
    }
    @Override
    public boolean delete(String rfc){
        return clientesRepository.deleteById(rfc);

    }
    @Override
    public boolean delete(int rfc){return false;}
    @Override
    public Clientes getById(int idCliente){return clientesRepository.findById(idCliente);}
}
