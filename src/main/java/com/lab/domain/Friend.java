package com.lab.domain;


/**
 * Reprezinta o relatie de prietenie intre doi utilizatori ai retelei
 */
public class Friend extends Entity<Integer> {
    private Integer idFriend1;
    private Integer idFriend2;

    public Friend(Integer id, Integer idFriend1, Integer idFriend2) {
        setId(id);
        this.idFriend1 = idFriend1;
        this.idFriend2 = idFriend2;
    }

    public Friend(Integer idFriend1, Integer idFriend2) {
        this.idFriend1 = idFriend1;
        this.idFriend2 = idFriend2;
    }

    public Integer getIdFriend1() {
        return idFriend1;
    }

    public void setIdFriend1(Integer idFriend1) {
        this.idFriend1 = idFriend1;
    }

    public Integer getIdFriend2() {
        return idFriend2;
    }

    public void setIdFriend2(Integer idFriend2) {
        this.idFriend2 = idFriend2;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + getId() +
                ", idFriend1=" + idFriend1 +
                ", idFriend2=" + idFriend2 +
                '}';
    }
}
