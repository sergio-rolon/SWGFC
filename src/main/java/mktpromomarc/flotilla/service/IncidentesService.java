package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Incidentes;
import mktpromomarc.flotilla.repository.IncidentesRepository;
import org.json.JSONArray;


public class IncidentesService implements ICrudService<Incidentes> {

    private IncidentesRepository incidentesRepository;

    public IncidentesService(IncidentesRepository incidentesRepository){
        this.incidentesRepository=incidentesRepository;
    }

    @Override
    public JSONArray getAll(){
        return incidentesRepository.findAll();
    }
    public JSONArray getAll(boolean isAsesor, String emailAsesor){
        return incidentesRepository.findAll(isAsesor, emailAsesor);
    }

    @Override
    public Incidentes getById(String serieIncidente){
        return incidentesRepository.findById(serieIncidente);
    }
    @Override
    public Incidentes add(Incidentes servicio){
        Incidentes servicioResult = null;
        if(!incidentesRepository.existsById(servicio.getIdIncidente())) {
            servicioResult = incidentesRepository.save(servicio);
        }
        return servicioResult;
    }

    @Override
    public Incidentes update(Incidentes servicio){

        if(!incidentesRepository.existsById(servicio.getIdAsignacion())) {
            return null;
        }
        return incidentesRepository.save(servicio);
    }

    //    public Asignaciones add(Asignaciones asignacion, int idCliente){
//        Asignaciones asignacionResult = null;
//        if(asignacion.getIdAsignacion() ==0){
//            asignacionResult = asignacionesRepository.save(asignacion, idCliente);
//        }else if(!asignacionesRepository.existsById(asignacion.getIdAsignacion())) {
//            asignacionResult = asignacionesRepository.save(asignacion, idCliente);
//
//        }
//        return asignacionResult;
//    }
    @Override
    public boolean delete(String idIncidente){
        return incidentesRepository.deleteById(idIncidente);
    }
    @Override
    public boolean delete(int idIncidente){return incidentesRepository.deleteById(idIncidente);}
    @Override
    public Incidentes getById(int idIncidente){return incidentesRepository.findById(idIncidente);}
}
