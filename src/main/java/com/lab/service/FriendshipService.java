package com.lab.service;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.exceptions.EmptyListException;
import com.lab.repository.Repository;


/**
 * Service pentru gestionarea relatiilor de prietenie dintre utilizatori
 */
public class FriendshipService {
    private final Repository<Tuple<Long , Long>, Friendship> friendRepository;

    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendRepository) {
        this.friendRepository = friendRepository;
    }

    public void add(Long idUser1 , Long idUser2){
        Friendship friendship = new Friendship(idUser1 ,idUser2);
        friendRepository.save(friendship);
    }


    public void remove(Tuple<Long,Long> id){
        friendRepository.delete(id);
    }

    public Iterable<Friendship> findAll(){
        Iterable<Friendship> friendships = friendRepository.findAll();
        if (friendships.iterator().hasNext()){
            throw new EmptyListException("There are no friendships added ");
        }
        return friendships;
    }
}
