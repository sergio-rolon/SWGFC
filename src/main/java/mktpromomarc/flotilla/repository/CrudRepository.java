package mktpromomarc.flotilla.repository;

import org.json.JSONArray;

public interface CrudRepository<T> {
    JSONArray findAll();
    T findById(String id);
    T findById(int id);
    boolean existsById(String id);
    T save(T entity);
    boolean deleteById(String id);
    boolean deleteById(int id);

}
