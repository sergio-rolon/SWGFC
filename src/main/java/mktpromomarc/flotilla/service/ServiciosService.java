package mktpromomarc.flotilla.service;

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
//        if(!serviciosRepository.existsById(servicio.getIdServicio())) {
//            if (!serviciosRepository.existByNumeroSerie(servicio.getIdVehiculo())) {
//                servicioResult = serviciosRepository.save(servicio);
//            }else{
//                servicioResult=new Servicios();
//                servicioResult.setIdServicio(Integer.parseInt("-1"));
//            }
//        }
        return servicioResult;
    }

    @Override
    public Servicios update(Servicios servicio){
        Servicios servicioRecovered = serviciosRepository.findById(servicio.getIdServicio());
//        if(!servicio.getIdServicio().equals(servicioRecovered.getIdServicio())){
//            if(serviciosRepository.existsById(servicio.getIdServicio())) {
//                return null;
//            }
//        }
        return serviciosRepository.save(servicio);
    }
    @Override
    public boolean delete(String serieServicio){
        return serviciosRepository.deleteById(serieServicio);

    }
    @Override
    public boolean delete(int serieServicio){return false;}
    @Override
    public Servicios getById(int idServicio){return serviciosRepository.findById(idServicio);}
}
