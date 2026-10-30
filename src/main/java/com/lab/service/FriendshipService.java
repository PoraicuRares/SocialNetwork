package com.lab.service;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.Repository;


/**
 * Service class responsible for managing friendship relations between users
 */
public class FriendshipService {
    private final Repository<Tuple<Long , Long>, Friendship> friendRepository;

    public FriendshipService(Repository<Tuple<Long, Long>, Friendship> friendRepository) {
        this.friendRepository = friendRepository;
    }

    /**
     * Creates and saves a new friendship between two users
     * @param idUser1 the ID of the first user
     * @param idUser2 the ID of the second user
     * @throws ValidationException if the users are the same or IDs are null
     * @throws RepositoryException if the friendship already exists
     */
    public void add(Long idUser1 , Long idUser2){
        Friendship friendship = new Friendship(idUser1 ,idUser2);
        friendRepository.save(friendship);
    }

    /**
     * Removes an existing friendship from the network
     * @param id a Tuple containing the IDs of the two friends
     * @throws RepositoryException if the friendship does not exist
     */
    public void remove(Tuple<Long,Long> id){
        Long left = Math.min(id.getLeft(), id.getRight());
        Long right = Math.max(id.getLeft() , id.getRight());
        Tuple<Long,Long> normalizedId = new Tuple<>(left , right);

        friendRepository.findOne(normalizedId)
                .orElseThrow(() -> new RepositoryException("There is no friendship with this id"));
        friendRepository.delete(normalizedId);
    }

    /**
     * Retrieves all friendships currently established in the network
     * @return an Iterable containing all friendships
     * @throws EmptyListException if there are no friendships in the network
     */
    public Iterable<Friendship> findAll(){
        Iterable<Friendship> friendships = friendRepository.findAll();
        if (!friendships.iterator().hasNext()){
            throw new EmptyListException("There are no friendships added ");
        }
        return friendships;
    }
}
