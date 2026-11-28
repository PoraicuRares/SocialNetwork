package com.lab.domain;

import java.time.LocalDateTime;

public class FriendshipRequest extends  Entity<Long>{
    private final Long fromId;
    private final Long toId;
    private FriendshipStatus status;
    private LocalDateTime date;

    public FriendshipRequest(Long fromId, Long toId) {
        this.fromId = fromId;
        this.toId=toId;
        this.status = FriendshipStatus.PENDING;
        this.date = LocalDateTime.now();
    }

    public FriendshipRequest(Long id ,Long fromId, Long toId, FriendshipStatus status, LocalDateTime date) {
        super.setId(id);
        this.fromId = fromId;
        this.toId = toId;
        this.status = status;
        this.date = date;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
