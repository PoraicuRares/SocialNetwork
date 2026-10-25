package com.lab.service;

import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.Repository;

/**
 * Service class responsible for managing user-related operations
 */
public class UserService {
    private final Repository<Long , User> userRepository;
    private long lastId;
    public UserService(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
        initializeLastId();
    }

    private void initializeLastId(){
        for(User user :userRepository.findAll()){
            if(user.getId() != null  && user.getId() > lastId){
                lastId = user.getId();
            }
        }
    }

    /**
     * Adds a new user to the network. The user's ID is automatically generated
     * @param user the user to be added
     * @throws ValidationException if the user attributes are invalid
     * @throws RepositoryException if an error occurs during saving
     */
    public void add(User user){
        lastId++;
        user.setId(lastId);
        userRepository.save(user);
    }


    /**
     * Removes a user from the network based on their ID
     * @param id the identifier of the user to be removed
     * @throws RepositoryException if the user with the given ID does not exist
     */
    public void remove(Long id ){
        userRepository.delete(id);
    }

    /**
     * Finds a user by their ID
     * @param id the identifier of the user to search for
     * @return the User if found, or null otherwise
     */
    public User findOne(Long id){
        return userRepository.findOne(id);
    }

    /**
     * Retrieves all users currently registered in the network
     * @return an Iterable containing all users
     * @throws EmptyListException if there are no users in the network
     */
    public Iterable<User> findAll(){
        Iterable<User> users = userRepository.findAll();
        if(!users.iterator().hasNext()){
            throw new EmptyListException("There are no users added");
        }
        return users;
    }
}
