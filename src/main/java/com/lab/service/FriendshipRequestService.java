package com.lab.service;

import com.lab.domain.FriendshipRequest;
import com.lab.domain.FriendshipStatus;
import com.lab.exceptions.ServiceException;
import com.lab.repository.Repository;
import com.lab.util.observer.AbstractObservable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Sends friendship requests and answers them, following the rules of the network.
 * A friendship is created only when the invited user approves the request.
 */
public class FriendshipRequestService extends AbstractObservable {
    private final Repository<Long, FriendshipRequest> requestRepository;
    private final FriendshipService friendshipService;
    private final UserService userService;

    public FriendshipRequestService(Repository<Long, FriendshipRequest> requestRepository, FriendshipService friendshipService, UserService userService) {
        this.requestRepository = requestRepository;
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    public FriendshipRequest sendRequest(Long fromId , Long toId){
        requireUser(fromId);
        requireUser(toId);
        if(fromId.equals(toId)){
            throw new ServiceException("You cannot send a friendship request to yourself");
        }
        if(friendshipService.getFriendsId(fromId).contains(toId)){
            throw new ServiceException("You are already friends with user " + toId);
        }
        if(existsPendingBetween(fromId, toId)){
            throw new ServiceException("There is already a pending request between users " + fromId + " and " + toId);
        }
        FriendshipRequest request = new FriendshipRequest(fromId,toId);
        requestRepository.save(request);
        notifyObservers();
        return request;
    }

    public FriendshipRequest approve(Long requestId , Long currentUserId){
        FriendshipRequest request = requireAnswerable(requestId,currentUserId);
        friendshipService.add(request.getFromId() , request.getToId());
        return changeStatus(request, FriendshipStatus.APPROVED);
    }

    public FriendshipRequest reject(Long requestId , Long currentUserId){
        FriendshipRequest request = requireAnswerable(requestId, currentUserId);
        return changeStatus(request,FriendshipStatus.REJECTED);
    }

    public List<FriendshipRequest> findReceivedRequests(Long userId){
        return requestRepository.stream()
                .filter(request->request.getStatus() == FriendshipStatus.PENDING)
                .filter(request -> request.getToId().equals(userId))
                .sorted(Comparator.comparing(FriendshipRequest::getDate).reversed())
                .toList();
    }

    private FriendshipRequest changeStatus(FriendshipRequest request ,FriendshipStatus status){
        request.setStatus(status);
        requestRepository.update(request);
        notifyObservers();
        return request;
    }

    private FriendshipRequest requireAnswerable(Long requestId , Long currentUserId){
        FriendshipRequest request = requestRepository.findOne(requestId)
                .orElseThrow(() -> new ServiceException("There is no friendship request with id " +requestId));
        if(!request.getToId().equals(currentUserId)){
            throw new ServiceException("Only the invited user can answer this request");
        }
        if(request.getStatus() != FriendshipStatus.PENDING){
            throw new ServiceException("This request was already answered: " + request.getStatus());
        }
        return request;
    }

    private boolean existsPendingBetween(Long firstId, Long secondId){
        return requestRepository.stream()
                .filter(request->request.getStatus() == FriendshipStatus.PENDING)
                .anyMatch(request->
                        (request.getFromId().equals(firstId) && request.getToId().equals(secondId))
                        || (request.getFromId().equals(secondId) && request.getToId().equals(firstId)));
    }
    private void requireUser(Long userId){
        userService.findOne(userId)
                .orElseThrow(()-> new ServiceException("There is no user with id " + userId));
    }
}
