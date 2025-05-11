package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Vehiculos;
import mktpromomarc.flotilla.repository.VehiculosRepository;
import org.json.JSONArray;


public class VehiculosService implements ICrudService<Vehiculos> {

    private VehiculosRepository vehiculosRepository;

    public VehiculosService(VehiculosRepository vehiculosRepository){
        this.vehiculosRepository=vehiculosRepository;
    }

    @Override
    public JSONArray getAll(){
        return vehiculosRepository.findAll();
    }
    @Override
    public Vehiculos getById(String numeroSerie){
        return vehiculosRepository.findById(numeroSerie);
    }
    @Override
    public Vehiculos add(Vehiculos vehiculo){
        Vehiculos vehiculoResult = null;
        if(!vehiculosRepository.existsById(vehiculo.getNumeroSerie())) {
            vehiculoResult = vehiculosRepository.save(vehiculo);
        }
        return vehiculoResult;
    }

    @Override
    public Vehiculos update(Vehiculos vehiculo){
        Vehiculos vehiculoRecovered = vehiculosRepository.findById(vehiculo.getIdVehiculo());
        if(!vehiculo.getNumeroSerie().equals(vehiculoRecovered.getNumeroSerie())){
            if(vehiculosRepository.existsById(vehiculo.getNumeroSerie())) {
                return null;
            }
        }
        return vehiculosRepository.save(vehiculo);
    }
    @Override
    public boolean delete(String numeroSerie){
        return vehiculosRepository.deleteById(numeroSerie);

    }
    @Override
    public boolean delete(int numeroSerie){return false;}
    @Override
    public Vehiculos getById(int idVehiculo){return vehiculosRepository.findById(idVehiculo);}
}
