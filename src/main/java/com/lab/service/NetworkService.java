package com.lab.service;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service class responsible for complex business logic,
 * graph analysis, and maintaining consistency between users and friendships
 */
public class NetworkService {
    private final FriendshipService friendshipService;
    private final UserService userService;

    public NetworkService(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    private Map<Long, List<Long>> buildGraph(){
        Map<Long , List<Long>> adj = new HashMap<>();
        for(User users : userService.findAll()){
            adj.put(users.getId() ,new ArrayList<>());
        }

        for(Friendship friendship : friendshipService.findAll()){
            Long id1 = friendship.getId().getLeft();
            Long id2 =friendship.getId().getRight();
            adj.get(id1).add(id2);
            adj.get(id2).add(id1);
        }
        return adj;
    }

    private void dfs(long node  , Map<Long , List<Long>> adj , Map<Long , Boolean> visited , List<Long> component){
        component.add(node);
        visited.put(node , true);
        for(long neighbor : adj.get(node)){
            if(!visited.containsKey(neighbor)){
                dfs(neighbor , adj , visited , component);
            }
        }
    }

    /**
     * Determines the total number of connected components (communities) in the social network
     * @return the integer number of communities
     */
    public int getNumberOfCommunities(){
        Map<Long , List<Long>> adj = buildGraph();
        Map<Long , Boolean> visited = new HashMap<>();
        int nrOfCommunities = 0;
        for(User users : userService.findAll()){
            if(!visited.containsKey(users.getId())){
                dfs(users.getId(), adj , visited , new ArrayList<>());
                nrOfCommunities++;
            }
        }
        return nrOfCommunities;
    }

    private int findLongestPath(long node , Map<Long , List<Long>> adj , Map<Long , Boolean> visited){
       visited.put(node , true);
       int mx = 0 ;
        for(Long neighbor : adj.get(node)){
            if(!visited.containsKey(neighbor)){
                int local = findLongestPath( neighbor ,adj , visited);
                mx = Math.max(mx , local);
            }
        }
        visited.remove(node);
        return 1+ mx;
    }


    private List<User> convertIdsToUsers(List<Long> ids){
        List<User> users = new ArrayList<>();
        for(Long id  : ids){
            users.add(userService.findOne(id).orElseThrow());
        }
        return users;
    }

    /**
     * Finds the most sociable community, defined as the connected component
     * containing the longest path in the graph
     * @return a List of users belonging to the most sociable community
     * @throws EmptyListException if there are no friendships in the network
     */
    public List<User> getMostPopularCommunity(){
        if(!friendshipService.findAll().iterator().hasNext()){
            throw new EmptyListException("There are no friendships added");
        }
        List<Long> mostPopularCommunity = new ArrayList<>();
        Map<Long , List<Long>> adj = buildGraph();

        int mx = 0;
        long node =  0;
        for(User user : userService.findAll()){
            Map<Long , Boolean> visited = new HashMap<>();
            int local = findLongestPath(user.getId() ,adj, visited);
            if(mx  < local){
                mx = local;
                node =  user.getId();
            }
        }
        Map<Long , Boolean> visited = new HashMap<>();
        dfs(node , adj , visited , mostPopularCommunity);


        return convertIdsToUsers(mostPopularCommunity);
    }

    /**
     * Adds a friendship between two existing users
     * @param id1 the identifier of the first user
     * @param id2 the identifier of the second user
     * @throws ServiceException if one or both users do not exist
     */
    public void addFriendship(Long id1, Long id2){
        if(userService.findOne(id1).isEmpty()  || userService.findOne(id2).isEmpty() )
            throw new ServiceException("One or both users do not exist");
        friendshipService.add(id1 , id2);
    }

    private void deleteUserFriendships(Long userId){
        Iterable<Friendship> friendships;
        try{
            friendships = friendshipService.findAll();
        }catch(EmptyListException e){
            return;
        }

        List<Tuple<Long,Long>> toDelete = new ArrayList<>();
        for(Friendship friendship: friendships){
            Tuple<Long , Long> id = friendship.getId();
            if(id.getLeft().equals(userId)|| id.getRight().equals(userId))
                toDelete.add(id);
        }
        for(Tuple<Long ,Long> id : toDelete){
            friendshipService.remove(id);
        }
    }

    /**
     * Deletes a user and triggers a cascading deletion of all their friendships
     * @param userId the identifier of the user to delete
     * @throws RepositoryException if the user does not exist
     */
    public void deleteUser(Long userId){
        deleteUserFriendships(userId);
        userService.remove(userId);
    }
}
