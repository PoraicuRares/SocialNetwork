package com.lab.service;

import com.lab.domain.Friend;
import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.ServiceException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.Repository;
import com.lab.validator.Validator;

import java.util.List;

/**
 * Service pentru gestionarea relatiilor de prietenie dintre utilizatori
 */
public class FriendshipService {
    private final Repository<Integer , Friend> friendRepository;
    private final Repository<Integer , User> userRepository;
    private final Validator<Friend> validator;

    public FriendshipService(Repository<Integer, Friend> friendRepository, Repository<Integer, User> userRepository, Validator<Friend> validator) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }

    /**
     * Creeaza si salveaza o relatie de prietenie intre doi utilizatori existenti
     * @param idFriend1 ID-ul primului utilizator
     * @param idFriend2 ID-ul celui de-al doilea utilizator
     * @throws ServiceException daca unul sau ambii utilizatori nu exista in retea
     * @throws ValidationException daca prietenia nu este valida (ex: acelasi ID)
     */
    public void add(Integer idFriend1, Integer idFriend2){
        if(userRepository.findOne(idFriend1) == null || userRepository.findOne(idFriend2) == null){
            throw new ServiceException("Unul sau ambii prieteni nu exista!");
        }
        Friend newFriend = new Friend(idFriend1, idFriend2);
        validator.validate(newFriend);
        friendRepository.save(newFriend);
    }

    /**
     * Sterge o prietenie din sistem
     * @param id identificatorul unic al prieteniei care trebuie stearsa
     */
    public void remove(Integer id){
        friendRepository.delete(id);
    }

    /**
     * Returneaza toate relatiile de prietenie din sistem
     * @return o lista cu toate prieteniile
     * @throws EmptyListException daca nu exista nicio prietenie salvata
     */
    public List<Friend> findAll(){
        if (friendRepository.findAll().isEmpty()){
                throw new EmptyListException("Nu exista nici o prietenie introdusa!");
        }
        return friendRepository.findAll();
    }
}
