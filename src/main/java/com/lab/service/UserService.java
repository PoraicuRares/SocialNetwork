package com.lab.service;

import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.repository.Repository;

/**
 * Service pentru gestionarea operatiunilor legate de utilizatori
 */
public class UserService {
    private final Repository<Long , User> userRepository;

    public UserService(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
    }

    public void add(User user){
        userRepository.save(user);
    }

    public void remove(Long id ){
        userRepository.delete(id);
    }

    public User findOne(Long id){
        return userRepository.findOne(id);
    }

    public Iterable<User> findAll(){
        Iterable<User> users = userRepository.findAll();
        if(users.iterator().hasNext()){
            throw new EmptyListException("There are no users added");
        }
        return users;
    }
}
