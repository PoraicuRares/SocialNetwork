package com.lab.service;

import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.Repository;
import com.lab.validator.Validator;

import java.util.List;

/**
 * Service pentru gestionarea operatiunilor legate de utilizatori
 */
public class UserService {
    private final Repository<Integer , User> userRepository;
    private final Validator<User> validator;

    public UserService(Repository<Integer, User> userRepository, Validator<User> validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    /**
     * Adauga un nou utilizator in sistem dupa ce il valideaza
     * @param firstName prenumele utilizatorului
     * @param lastName numele de familie al utilizatorului
     * @param age varsta utilizatorului
     * @throws ValidationException daca datele utilizatorului nu sunt valide
     */
    public void add(String firstName , String lastName , Integer age){
        User newUser = new User(age , firstName , lastName);
        validator.validate(newUser);
        userRepository.save(newUser);
    }

    /**
     * Sterge un utilizator din sistem pe baza ID-ului
     * @param id identificatorul utilizatorului de sters
     */
    public void remove(Integer id ){
        userRepository.delete(id);
    }

    /**
     * Returneaza toti utilizatorii inregistrati in sistem
     * @return o lista cu toti utilizatorii
     * @throws EmptyListException daca nu exista niciun utilizator
     */
    public List<User> findAll(){
        if(userRepository.findAll().isEmpty()){
            throw new EmptyListException("Nu exista nici un utilizator introdus!");
        }
        return userRepository.findAll();
    }
}
