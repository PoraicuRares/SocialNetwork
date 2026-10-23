package com.lab.repository;

import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class  InMemoryRepository< E extends Entity<Integer>> implements Repository<Integer , E>{
    protected Map<Integer , E> entities = new HashMap<>();
    protected Integer lastId = 0;

    @Override
    public void save(E entity) {
        lastId++;
        entity.setId(lastId);
        entities.put(entity.getId() ,entity);
    }

    @Override
    public void delete(Integer id) {
        if(!entities.containsKey(id)){
            throw new RepositoryException("Nu exista un entitate cu acesst id!");
        }
        entities.remove(id);
    }

    @Override
    public List<E> findAll() {
        return entities.values().stream().toList();
    }

    @Override
    public E findOne(Integer id) {
        if(!entities.containsKey(id)){
            return null;
        }
        return entities.get(id);
    }
}
