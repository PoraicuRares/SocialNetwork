package com.lab.service;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.ServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service class responsible for graph analysis and algorithms on the social network.
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

    
    public int getNumberOfCommunities(){
        Map<Long , List<Long>> adj = buildGraph();
        Map<Long , Boolean> visited = new HashMap<>();
        int nrOfComunities = 0;
        for(User users : userService.findAll()){
            if(!visited.containsKey(users.getId())){
                dfs(users.getId(), adj , visited , new ArrayList<>());
                nrOfComunities++;
            }
        }
        return nrOfComunities;
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
            users.add(userService.findOne(id));
        }
        return users;
    }

    
    public List<User> getMostPopularCommunity(){
        if(friendshipService.findAll().iterator().hasNext()){
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

    public void addFriendship(Long id1, Long id2){
        if(userService.findOne(id1) == null || userService.findOne(id2) == null)
            throw new ServiceException("One or both users do not exist");
        friendshipService.add(id1 , id2);
    }

    private void deleteUserFriendships(Long userId){
        Iterable<Friendship> friendships = friendshipService.findAll();
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
    public void deleteUser(Long userId){
        deleteUserFriendships(userId);
        userService.remove(userId);
    }
}
