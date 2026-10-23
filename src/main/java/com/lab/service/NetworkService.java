package com.lab.service;

import com.lab.domain.Friend;
import com.lab.domain.User;
import com.lab.exceptions.EmptyListException;
import com.lab.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Clasa de service responsabila cu analiza si algoritmii pe grafuri ai retelei de socializare
 */
public class NetworkService {
    private final Repository<Integer , User> userRepository;
    private final Repository<Integer , Friend> friendRepository;

    public NetworkService(Repository<Integer, User> userRepository, Repository<Integer, Friend> friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    private Map<Integer, List<Integer>> buildGraph(){
        Map<Integer , List<Integer>> adj = new HashMap<>();
        for(User users : userRepository.findAll()){
            adj.put(users.getId() ,new ArrayList<>());
        }

        for(Friend friendship : friendRepository.findAll()){
            Integer id1 = friendship.getIdFriend1();
            Integer id2 =friendship.getIdFriend2();
            adj.get(id1).add(id2);
            adj.get(id2).add(id1);
        }
        return adj;
    }

    private void dfs(int node  , Map<Integer , List<Integer>> adj , Map<Integer , Boolean> visited , List<Integer> component){
        component.add(node);
        visited.put(node , true);
        for(int neighbor : adj.get(node)){
            if(!visited.containsKey(neighbor)){
                dfs(neighbor , adj , visited , component);
            }
        }
    }

    /**
     * Calculeaza numarul total de comunitati (componente conexe) din retea.
     * Foloseste un algoritm de parcurgere in adancime (DFS)
     * @return numarul de comunitati identificate
     */
    public int getNumberOfCommunities(){
        Map<Integer , List<Integer>> adj = buildGraph();
        Map<Integer , Boolean> visited = new HashMap<>();
        int nrOfComunities = 0;
        for(User users : userRepository.findAll()){
            if(!visited.containsKey(users.getId())){
                dfs(users.getId(), adj , visited , new ArrayList<>());
                nrOfComunities++;
            }
        }
        return nrOfComunities;
    }

    private int findLongestPath(int node , Map<Integer , List<Integer>> adj , Map<Integer , Boolean> visited){
       visited.put(node , true);
       int mx = 0 ;
        for(Integer neighbor : adj.get(node)){
            if(!visited.containsKey(neighbor)){
                int local = findLongestPath( neighbor ,adj , visited);
                mx = Math.max(mx , local);
            }
        }
        visited.remove(node);
        return 1+ mx;
    }


    private List<User> convertIdsToUsers(List<Integer> ids){
        List<User> users = new ArrayList<>();
        for(Integer id  : ids){
            users.add(userRepository.findOne(id));
        }
        return users;
    }

    /**
     * Determina cea mai sociabila comunitate (componenta conexa cu cel mai lung drum).
     * Utilizeaza un algoritm DFS combinat cu Backtracking pentru a gasi lungimea maxima.
     * @return o lista cu utilizatorii care fac parte din cea mai sociabila comunitate
     * @throws EmptyListException daca nu exista nicio prietenie in retea
     */
    public List<User> getMostPopularCommunity(){
        if(friendRepository.findAll().isEmpty()){
            throw new EmptyListException("Nu exista nici o prietenie introdusa");
        }
        List<Integer> mostPopularCommunity = new ArrayList<>();
        Map<Integer , List<Integer>> adj = buildGraph();

        int mx = 0;
        int node =  0;
        for(User user : userRepository.findAll()){
            Map<Integer , Boolean> visited = new HashMap<>();
            int local = findLongestPath(user.getId() ,adj, visited);
            if(mx  < local){
                mx = local;
                node =  user.getId();
            }
        }
        Map<Integer , Boolean> visited = new HashMap<>();
        dfs(node , adj , visited , mostPopularCommunity);


        return convertIdsToUsers(mostPopularCommunity);
    }
}
