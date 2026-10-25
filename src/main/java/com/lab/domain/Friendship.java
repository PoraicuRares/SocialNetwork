package com.lab.domain;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a relationship between two users in the social network
 */
public class Friendship extends Entity<Tuple<Long ,Long>> {
    private LocalDateTime date;

    public Friendship() {

    }

    public Friendship(Long idFriend1, Long idFriend2) {
        Long left = Math.min(idFriend1 , idFriend2);
        Long right = Math.max(idFriend1 , idFriend2);
        this.setId(new Tuple<>(left , right));
        this.date = LocalDateTime.now();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "First user id : " + getId().getLeft() + " | Second user id  : " + getId().getRight() + " | Date :" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    }
}
