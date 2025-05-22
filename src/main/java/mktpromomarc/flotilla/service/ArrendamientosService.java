package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Arrendamientos;
import mktpromomarc.flotilla.repository.ArrendamientosRepository;
import org.json.JSONArray;


public class ArrendamientosService implements ICrudService<Arrendamientos> {

    private ArrendamientosRepository arrendamientosRepository;

    public ArrendamientosService(ArrendamientosRepository arrendamientosRepository){
        this.arrendamientosRepository=arrendamientosRepository;
    }

    @Override
    public JSONArray getAll(){
        return arrendamientosRepository.findAll();
    }
    @Override
    public Arrendamientos getById(String numeroContrato){
        return arrendamientosRepository.findById(numeroContrato);
    }
    @Override
    public Arrendamientos add(Arrendamientos arrendamiento){
        Arrendamientos arrendamientoResult = null;
        if(!arrendamientosRepository.existsById(arrendamiento.getNumeroContrato())) {
            arrendamientoResult = arrendamientosRepository.save(arrendamiento);
        }
        return arrendamientoResult;
    }

    @Override
    public Arrendamientos update(Arrendamientos arrendamiento){
        Arrendamientos arrendamientoRecovered = arrendamientosRepository.findById(arrendamiento.getIdArrendamiento());
        if(!arrendamiento.getNumeroContrato().equals(arrendamientoRecovered.getNumeroContrato())){
            if(arrendamientosRepository.existsById(arrendamiento.getNumeroContrato())) {
                return null;
            }
        }
        return arrendamientosRepository.save(arrendamiento);
    }
    @Override
    public boolean delete(String numeroContrato){
        return arrendamientosRepository.deleteById(numeroContrato);

    }
    @Override
    public boolean delete(int numeroContrato){return false;}
    @Override
    public Arrendamientos getById(int idArrendamiento){return arrendamientosRepository.findById(idArrendamiento);}
}
