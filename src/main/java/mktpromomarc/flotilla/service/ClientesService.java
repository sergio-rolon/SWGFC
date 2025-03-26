package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Clientes;
import mktpromomarc.flotilla.repository.ClientesRepository;
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
        Clientes clienteResult = null;
        if(clientesRepository.existsById(rfc)) {
            return clienteResult = clientesRepository.findById(rfc);
        }
        return clienteResult;
    }
    @Override
    public Clientes add(Clientes cliente){
        Clientes clienteResult = null;
        if(!clientesRepository.existsById(cliente.getRfc())) {
            return clienteResult = clientesRepository.save(cliente);
        }
        return clienteResult;
    }

    @Override
    public Clientes update(Clientes cliente){
        Clientes clienteResult = null;
        if(clientesRepository.existsById(cliente.getRfc())) {
            cliente.setIdCliente(clientesRepository.findById(cliente.getRfc()).getIdCliente());
            return clienteResult = clientesRepository.save(cliente);
        }
        return clienteResult;
    }
    @Override
    public boolean delete(String rfc){
        boolean result = false;
        if(clientesRepository.existsById(rfc)) {
            clientesRepository.deleteById(rfc);
            result = true;
        }
        return result;
    }
    @Override
    public boolean delete(int rfc){return false;}
    @Override
    public Clientes getById(int rfc){return new Clientes();}
}
