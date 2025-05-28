package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Asignaciones;
import mktpromomarc.flotilla.repository.AsignacionesRepository;
import org.json.JSONArray;


public class AsignacionesService implements ICrudService<Asignaciones> {

    private AsignacionesRepository asignacionesRepository;

    public AsignacionesService(AsignacionesRepository asignacionesRepository){
        this.asignacionesRepository=asignacionesRepository;
    }

    @Override
    public JSONArray getAll(){
        return asignacionesRepository.findAll();
    }
    public JSONArray getAll(boolean isAsesor, String emailAsesor){
        return asignacionesRepository.findAll(isAsesor, emailAsesor);
    }
    // *************Por ver que onda
    @Override
    public Asignaciones getById(String idAsignacion){
        return asignacionesRepository.findById(idAsignacion);
    }
    @Override
    public Asignaciones add(Asignaciones asignacion){
        Asignaciones asignacionResult = null;
        if(!asignacionesRepository.existsById(asignacion.getIdAsignacion())) {
                asignacionResult = asignacionesRepository.save(asignacion);

        }
        return asignacionResult;
    }
    public Asignaciones add(Asignaciones asignacion, int idCliente){
        Asignaciones asignacionResult = null;
        if(asignacion.getIdAsignacion() ==0){
            asignacionResult = asignacionesRepository.save(asignacion, idCliente);
        }else if(!asignacionesRepository.existsById(asignacion.getIdAsignacion())) {
            asignacionResult = asignacionesRepository.save(asignacion, idCliente);

        }
        return asignacionResult;
    }
    @Override
    public Asignaciones update(Asignaciones asignacion){
        return null;
    }
    public Asignaciones update(Asignaciones asignacion, int idCliente){
        if(!asignacionesRepository.existsById(asignacion.getIdAsignacion())) {
            return null;
        }
        return asignacionesRepository.save(asignacion, idCliente);
    }
    @Override
    public boolean delete(String idAsignacion){
        return asignacionesRepository.deleteById(idAsignacion);

    }
    @Override
    public boolean delete(int idAsignacion){return asignacionesRepository.deleteById(idAsignacion);}
    @Override
    public Asignaciones getById(int idAsignacion){return asignacionesRepository.findById(idAsignacion);}
}

