package org.example.service;

import org.example.modelo.Clientes;
import org.example.repository.ClientesRepository;
import org.example.repository.UsuariosRepository;
import org.json.JSONArray;

import java.util.List;

public class ClientesService {

//    public static List<Clientes> getAllClientes(){
//        return ClientesRepository.findAll();
//    }
public static JSONArray getAllClientes(){
    return ClientesRepository.findAll();
}

    public static Clientes getClienteById(String rfc){
        Clientes clienteResult = null;
        if(ClientesRepository.existsById(rfc)) {
            return clienteResult = ClientesRepository.findById(rfc);
        }
        return clienteResult;
    }

    public static Clientes addCliente(Clientes cliente){
        Clientes clienteResult = null;
        if(!ClientesRepository.existsById(cliente.getRfc())) {
            return clienteResult = ClientesRepository.save(cliente);
        }
        return clienteResult;
    }


    public static Clientes updateCliente(Clientes cliente){
        Clientes clienteResult = null;
        if(ClientesRepository.existsById(cliente.getRfc())) {
            cliente.setIdCliente(ClientesRepository.findById(cliente.getRfc()).getIdCliente());
            return clienteResult = ClientesRepository.save(cliente);
        }
        return clienteResult;
    }

    public static boolean deleteCliente (String rfc){
        boolean result = false;
        if(ClientesRepository.existsById(rfc)) {
            ClientesRepository.deleteById(rfc);
            result = true;
        }
        return result;
    }
}
