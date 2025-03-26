package org.example.service;

import org.json.JSONArray;

public interface CrudService<T> {
    JSONArray getAll();
    T getById(String id);
    T add(T entity);
    T update(T entity);
    boolean delete(String id);

    T getById(int id);
    boolean delete(int id);

}
