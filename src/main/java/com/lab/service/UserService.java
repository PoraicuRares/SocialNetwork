package com.lab.service;

import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class responsible for managing user-related operations
 */
public class UserService {
    private final Repository<Long , User> userRepository;
    private long lastId;
    public UserService(Repository<Long, User> userRepository) {
        this.userRepository = userRepository;
        this.lastId = findMaxId();
    }

    private long findMaxId(){
        return userRepository.stream()
                .map(User::getId)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(0L);
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
        userRepository.findOne(id)
                .orElseThrow(() -> new RepositoryException("There is no user with this id"));
        userRepository.delete(id);
    }

    /**
     * Finds a user by their ID
     * @param id the identifier of the user to search for
     * @return the User if found, or null otherwise
     */
    public Optional<User> findOne(Long id){
        return userRepository.findOne(id);
    }

    /**
     * Retrieves all users currently registered in the network, sorted by id
     * @return a list containing all users, sorted by id
     * @throws EmptyListException if there are no users in the network
     */
    public List<User> findAll(){
        List<User> users = userRepository.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        if(users.isEmpty()){
            throw new EmptyListException("There are no users added");
        }
        return users;
    }

    /**
     * Retrieves the users that satisfy the given condition.
     * Unlike {@link #findAll()}, it does not throw if the result is empty.
     * @param filter the condition a user must satisfy to be included
     * @return a list with the matching users, possibly empty
     */
    public List<User> findAll(Predicate<User> filter){
        return userRepository.findAll(filter);
    }

    /**
     * Returns a new stream over all users, for further processing by other services.
     * Unlike {@link #findAll()}, it does not throw if there are no users.
     * @return a new stream of all users, possibly empty; it can be consumed only once
     */
    public Stream<User> stream(){
        return userRepository.stream();
    }
}
