package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Seguros;
import mktpromomarc.flotilla.repository.SegurosRepository;
import org.json.JSONArray;


public class SegurosService implements ICrudService<Seguros> {

    private SegurosRepository segurosRepository;

    public SegurosService(SegurosRepository segurosRepository){
        this.segurosRepository=segurosRepository;
    }

    @Override
    public JSONArray getAll(){
        return segurosRepository.findAll();
    }
    public JSONArray getAll(boolean isAsesor, String emailAsesor){
        return segurosRepository.findAll(isAsesor, emailAsesor);
    }

    @Override
    public Seguros getById(String numeroPoliza){
        return segurosRepository.findById(numeroPoliza);
    }
    @Override
    public Seguros add(Seguros seguro){
        Seguros seguroResult = null;
        if(!segurosRepository.existsById(seguro.getNumeroPoliza())) {
            if (!segurosRepository.existByNumeroSerie(seguro.getIdVehiculo())) {
                seguroResult = segurosRepository.save(seguro);
            }else{
                seguroResult=new Seguros();
                seguroResult.setIdSeguro(Integer.parseInt("-1"));
            }
        }
        return seguroResult;
    }

    @Override
    public Seguros update(Seguros seguro){
        Seguros seguroRecovered = segurosRepository.findById(seguro.getIdSeguro());
        if(!seguro.getNumeroPoliza().equals(seguroRecovered.getNumeroPoliza())){
            if(segurosRepository.existsById(seguro.getNumeroPoliza())) {
                return null;
            }
        }
        return segurosRepository.save(seguro);
    }
    @Override
    public boolean delete(String numeroPoliza){
        return segurosRepository.deleteById(numeroPoliza);

    }
    @Override
    public boolean delete(int numeroPoliza){return false;}
    @Override
    public Seguros getById(int idSeguro){return segurosRepository.findById(idSeguro);}
}
