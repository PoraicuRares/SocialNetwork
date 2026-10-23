package com.lab.domain;


import java.time.LocalDateTime;

/**
 * Reprezinta o relatie de prietenie intre doi utilizatori ai retelei
 */
public class Friend extends Entity<Tuple<Long ,Long>> {
    private LocalDateTime date;

    public Friend(Long idFriend1, Long idFriend2) {
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
}
