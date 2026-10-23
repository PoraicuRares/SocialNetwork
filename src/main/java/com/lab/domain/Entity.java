package com.lab.domain;

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
}
