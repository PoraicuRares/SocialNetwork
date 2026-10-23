package com.lab.repository;

import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.validator.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRepository<ID , E extends Entity<ID>> implements Repository<ID , E>{
    private Map<ID , E> entities = new HashMap<>();
    private Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
    }

    @Override
    public void save(E entity) {
        validator.validate(entity);
        if(entities.containsKey(entity.getId())){
            throw new RepositoryException("An entity with the same id already exists")
        }
        entities.put(entity.getId() ,entity);
    }

    @Override
    public void delete(ID id) {
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
