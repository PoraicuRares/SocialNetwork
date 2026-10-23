package com.lab.repository;


import com.lab.domain.Entity;


/**
 * Interfata CRUD generica pentru gestionarea datelor
 * @param <ID> tipul identificatorului entitatii
 * @param <E> tipul entitatii care extinde clasa Entity
 */
public interface Repository<ID , E extends Entity<ID>> {
    /**
     * Salveaza o entitate in repository
     * @param entity entitatea care trebuie salvata
     */
    void save(E entity);

    /**
     * Sterge entitatea cu identificatorul specificat.
     * @param id identificatorul entitatii care trebuie stearsa
     */
    void delete(ID id);

    /**
     * Cauta si returneaza o entitate pe baza identificatorului
     * @param id identificatorul entitatii cautate
     * @return entitatea gasita sau null daca aceasta nu exista
     */
    E findOne(ID id);

    /**
     * Returneaza o lista cu toate entitatile din repository
     * @return o lista continand toate entitatile
     */
    Iterable<E> findAll();
}
