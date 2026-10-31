package com.lab.service;

import com.lab.domain.Friendship;
import com.lab.domain.FriendshipCriteria;
import com.lab.domain.Tuple;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;
import com.lab.repository.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Service class responsible for managing friendship relations between users
 */
public class FriendshipService {
    private final Repository<Tuple<Long , Long>, Friendship> friendRepository;
    private static final Comparator<Friendship> BY_USERS_IDS =
            Comparator.comparing((Friendship f) -> f.getId().getLeft())
                    .thenComparing(f ->f.getId().getRight());

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
     * Retrieves all users currently registered in the network, sorted by id
     * @return a list containing all users, sorted by id
     * @throws EmptyListException if there are no users in the network
     */
    public List<Friendship> findAll(){
        List<Friendship> friendships = friendRepository.stream()
                .sorted(BY_USERS_IDS)
                .collect(Collectors.toList());
        if (friendships.isEmpty()){
            throw new EmptyListException("There are no friendships added ");
        }
        return friendships;
    }

    /**
     * Retrieves the friendships that satisfy the given condition.
     * Unlike {@link #findAll()}, it does not throw if the result is empty.
     * @param filter the condition a friendship must satisfy to be included
     * @return a list with the matching friendships, possibly empty
     */
    public List<Friendship> findAll(Predicate<Friendship> filter){
        return friendRepository.findAll(filter);
    }

    /**
     * Returns a new stream over all friendships, for further processing by other services.
     * Unlike {@link #findAll()}, it does not throw if there are no friendships.
     *
     * @return a new stream of all friendships, possibly empty; it can be consumed only once
     */
    public Stream<Friendship> stream(){
        return friendRepository.stream();
    }

    private Long otherUser(Friendship friendship , Long userId){
        Tuple<Long,Long> id = friendship.getId();
        if(id.getLeft().equals(userId)) {
            return id.getRight();
        }else{
            return id.getLeft();
        }
    }

    /**
     * Returns the identifiers of all friends of the given user.
     *
     * @param userId the identifier of the user
     * @return the set of ids of the users who are friends with {@code userId}, possibly empty
     */
    public Set<Long> getFriendsId(Long userId){
        return findAll(FriendshipCriteria.involves(userId)).stream()
                .map(friendship -> otherUser(friendship , userId))
                .collect(Collectors.toSet());
    }
}
