package com.lab.validator;


import com.lab.exceptions.ValidationException;

/**
 * Interfata generica pentru validarea entitatilor (Sablonul Strategy)
 * @param <T> tipul entitatii care urmeaza a fi validata
 */
public interface Validator <T>{
    /**
     * Valideaza o entitate data
     * @param entity entitatea care trebuie validata
     * @throws ValidationException daca entitatea nu respecta regulile de validare
     */
    void validate(T entity) throws ValidationException;

}
