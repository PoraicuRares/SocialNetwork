package com.lab.domain;

import java.util.Objects;

/**
 * Clasa de baza pentru toate entitatile din aplicatie
 * @param <ID> tipul de date al identificatorului unic
 */
public abstract class  Entity<ID>{
    private ID id;
    public ID getId(){
        return this.id;
    }
    public void setId(ID id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entity<?> entity)) return false;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
