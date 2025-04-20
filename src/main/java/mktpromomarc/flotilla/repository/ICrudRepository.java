package mktpromomarc.flotilla.repository;

import org.json.JSONArray;

public interface ICrudRepository<T> {
    JSONArray findAll();
    JSONArray findAllObjects();
    T findById(String id);
    T findById(int id);
    boolean existsById(String id);
    T save(T entity);
    boolean deleteById(String id);
    boolean deleteById(int id);

}
