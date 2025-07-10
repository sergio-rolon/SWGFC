package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Asignaciones;
import mktpromomarc.flotilla.modelo.Servicios;
import mktpromomarc.flotilla.repository.ServiciosRepository;
import org.json.JSONArray;


public class ServiciosService implements ICrudService<Servicios> {

    private ServiciosRepository serviciosRepository;

    public ServiciosService(ServiciosRepository serviciosRepository){
        this.serviciosRepository=serviciosRepository;
    }

    @Override
    public JSONArray getAll(){
        return serviciosRepository.findAll();
    }
    public JSONArray getAll(boolean isAsesor, String emailAsesor){
        return serviciosRepository.findAll(isAsesor, emailAsesor);
    }

    @Override
    public Servicios getById(String serieServicio){
        return serviciosRepository.findById(serieServicio);
    }
    @Override
    public Servicios add(Servicios servicio){
        Servicios servicioResult = null;
        if(!serviciosRepository.existsById(servicio.getIdServicio())) {
            servicioResult = serviciosRepository.save(servicio);
        }
        return servicioResult;
    }

    @Override
    public Servicios update(Servicios servicio){

        if(!serviciosRepository.existsById(servicio.getIdAsignacion())) {
            return null;
        }
        return serviciosRepository.save(servicio);
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
    public boolean delete(String idServicio){
        return serviciosRepository.deleteById(idServicio);
    }
    @Override
    public boolean delete(int idServicio){return serviciosRepository.deleteById(idServicio);}
    @Override
    public Servicios getById(int idServicio){return serviciosRepository.findById(idServicio);}
}
