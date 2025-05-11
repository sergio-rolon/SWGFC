package mktpromomarc.flotilla.service;

import mktpromomarc.flotilla.modelo.Empleados;
import mktpromomarc.flotilla.repository.EmpleadosRepository;
import org.json.JSONArray;


public class EmpleadosService implements ICrudService<Empleados> {

    private EmpleadosRepository empleadosRepository;

    public EmpleadosService(EmpleadosRepository empleadosRepository){
        this.empleadosRepository=empleadosRepository;
    }

    @Override
    public JSONArray getAll(){
        return empleadosRepository.findAll();
    }
    @Override
    public Empleados getById(String numeroTrabajador){
        return empleadosRepository.findById(numeroTrabajador);
    }
    @Override
    public Empleados add(Empleados empleado){
        Empleados empleadoResult = null;
        if(!empleadosRepository.existsById(empleado.getNumeroTrabajador())) {
            empleadoResult = empleadosRepository.save(empleado);
        }
        return empleadoResult;
    }

    @Override
    public Empleados update(Empleados empleado){
        Empleados empleadoRecovered = empleadosRepository.findById(empleado.getIdEmpleado());
        if(!empleado.getNumeroTrabajador().equals(empleadoRecovered.getNumeroTrabajador())){
            if(empleadosRepository.existsById(empleado.getNumeroTrabajador())) {
                return null;
            }
        }
        return empleadosRepository.save(empleado);
    }
    @Override
    public boolean delete(String numeroTrabajador){
        return empleadosRepository.deleteById(numeroTrabajador);

    }
    @Override
    public boolean delete(int numeroTrabajador){return false;}
    @Override
    public Empleados getById(int idEmpleado){return empleadosRepository.findById(idEmpleado);}
}
