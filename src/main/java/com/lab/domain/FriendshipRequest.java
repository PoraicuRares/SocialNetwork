package com.lab.domain;

import java.time.LocalDateTime;

public class FriendshipRequest extends  Entity<Long>{
    private final Long from;
    private final Long to;
    private FriendshipStatus status;
    private LocalDateTime date;

    public FriendshipRequest(Long from, Long aLong) {
        this.from = from;
        to = aLong;
    }
}
