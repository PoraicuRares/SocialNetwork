package com.lab.service;

import com.lab.domain.Friendship;
import com.lab.domain.FriendshipCriteria;
import com.lab.domain.User;
import com.lab.domain.UserCriteria;
import com.lab.exceptions.EmptyListException;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ServiceException;
import com.lab.util.paging.Page;
import com.lab.util.paging.Pageable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    private Map<Long, Set<Long>> buildGraph() {
        return userService.stream()
                .collect(Collectors.toMap(
                        User::getId,
                        user -> friendshipService.getFriendsId(user.getId())));
    }

    private Set<Long> neighbors(Long node, Map<Long, Set<Long>> graph) {
        return graph.getOrDefault(node, Set.of());
    }

    private void visit(Long node, Map<Long, Set<Long>> graph, Set<Long> component) {
        component.add(node);
        neighbors(node, graph).stream()
                .filter(neighbor -> !component.contains(neighbor))
                .forEach(neighbor -> visit(neighbor, graph, component));
    }

    private Set<Long> componentOf(Long start, Map<Long, Set<Long>> graph) {
        Set<Long> component = new HashSet<>();
        visit(start, graph, component);
        return component;
    }

    private Stream<Set<Long>> communities(Map<Long, Set<Long>> graph) {
        return graph.keySet().stream()
                .map(id -> componentOf(id, graph))
                .distinct();
    }

    /**
     * Determines the total number of connected components (communities) in the social network.
     * A user without friends forms a community on their own.
     *
     * @return the number of communities, 0 if there are no users
     */
    public int getNumberOfCommunities() {
        return (int) communities(buildGraph()).count();
    }

    private Set<Long> extend(Set<Long> path, Long node) {
        Set<Long> newPath = new HashSet<>(path);
        newPath.add(node);
        return newPath;
    }

    private int longestPathFrom(Long node, Map<Long, Set<Long>> graph, Set<Long> path) {
        return 1 + neighbors(node, graph).stream()
                .filter(neighbor -> !path.contains(neighbor))
                .mapToInt(neighbor -> longestPathFrom(neighbor, graph, extend(path, neighbor)))
                .max()
                .orElse(0);
    }

    private int longestPathIn(Set<Long> community, Map<Long, Set<Long>> graph) {
        return community.stream()
                .mapToInt(node -> longestPathFrom(node, graph, Set.of(node)))
                .max()
                .orElse(0);
    }

    /**
     * Finds the most sociable community, defined as the connected component
     * containing the longest path in the graph
     *
     * @return the users of the most sociable community, sorted by id
     * @throws EmptyListException if there are no friendships in the network
     */
    public List<User> getMostPopularCommunity() {
        if (friendshipService.stream().findAny().isEmpty())
            throw new EmptyListException("There are no friendships added");
        Map<Long, Set<Long>> graph = buildGraph();
        Set<Long> mostSociable = communities(graph)
                .max(Comparator.comparingInt(community -> longestPathIn(community, graph)))
                .orElseThrow();

        return userService.findAll(UserCriteria.hasIdIn(mostSociable)).stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    /**
     * Adds a friendship between two existing users
     *
     * @param id1 the identifier of the first user
     * @param id2 the identifier of the second user
     * @throws ServiceException if one or both users do not exist
     */
    public void addFriendship(Long id1, Long id2) {
        if (userService.findOne(id1).isEmpty() || userService.findOne(id2).isEmpty())
            throw new ServiceException("One or both users do not exist");
        friendshipService.add(id1, id2);
    }

    /**
     * Deletes a user and triggers a cascading deletion of all their friendships
     *
     * @param userId the identifier of the user to delete
     * @throws RepositoryException if the user does not exist
     */
    public void deleteUser(Long userId) {
        friendshipService.findAll(FriendshipCriteria.involves(userId)).stream()
                .map(Friendship::getId)
                .forEach(friendshipService::remove);
        userService.remove(userId);
    }

    /**
     * Returns one page of the friends of a user, sorted by id. With the database storage the page
     * is built in SQL; with the other storages it is built here from the friend ids and the users
     *
     * @param userId     the id of the user
     * @param pageNumber the index of the page, starting from 0
     * @param pageSize   the number of friends on a page
     * @return the friends on the page and the total number of friends; an empty page if it is after the last one
     * @throws IllegalArgumentException if {@code pageNumber} is negative or {@code pageSize} is not positive
     * @throws RepositoryException      if the storage cannot be accessed
     */
    public Page<User> findFriendsOnPage(Long userId, int pageNumber, int pageSize) {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Page number or size invalid");
        }
        Pageable pageable = new Pageable(pageNumber, pageSize);
        return friendshipService.findFriendsOnPageInStorage(userId, pageable)
                .orElseGet(() -> buildFriendsPage(userId, pageable));

    }

    private Page<User> buildFriendsPage(Long userId, Pageable pageable) {
        List<Long> list = friendshipService.getFriendsId(userId)
                .stream()
                .sorted()
                .toList();

        List<User> userList = list.stream()
                .map(id -> userService.findOne(id)
                        .orElseThrow(() -> new ServiceException("The user with this id does not exist!")))
                .skip((long) pageable.pageNumber() * pageable.pageSize())
                .limit(pageable.pageSize())
                .toList();

        return new Page<>(userList, list.size());
    }
}
