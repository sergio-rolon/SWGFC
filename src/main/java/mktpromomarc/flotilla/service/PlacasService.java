package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Placas;
import mktpromomarc.flotilla.repository.PlacasRepository;
import org.json.JSONArray;


public class PlacasService implements ICrudService<Placas> {

    private PlacasRepository placasRepository;

    public PlacasService(PlacasRepository placasRepository){
        this.placasRepository=placasRepository;
    }

    @Override
    public JSONArray getAll(){
        return placasRepository.findAll();
    }
    public JSONArray getAll(boolean isAsesor, String emailAsesor){
        return placasRepository.findAll(isAsesor, emailAsesor);
    }

    @Override
    public Placas getById(String seriePlaca){
        return placasRepository.findById(seriePlaca);
    }
    @Override
    public Placas add(Placas placa){
        Placas placaResult = null;
        if(!placasRepository.existsById(placa.getSeriePlaca())) {
            if (!placasRepository.existByNumeroSerie(placa.getIdVehiculo())) {
                placaResult = placasRepository.save(placa);
            }else{
                placaResult=new Placas();
                placaResult.setIdPlaca(Integer.parseInt("-1"));
            }
        }
        return placaResult;
    }

    @Override
    public Placas update(Placas placa){
        Placas placaRecovered = placasRepository.findById(placa.getIdPlaca());
        if(!placa.getSeriePlaca().equals(placaRecovered.getSeriePlaca())){
            if(placasRepository.existsById(placa.getSeriePlaca())) {
                return null;
            }
        }
        return placasRepository.save(placa);
    }
    @Override
    public boolean delete(String seriePlaca){
        return placasRepository.deleteById(seriePlaca);

    }
    @Override
    public boolean delete(int seriePlaca){return false;}
    @Override
    public Placas getById(int idPlaca){return placasRepository.findById(idPlaca);}
}
