package com.lab.domain;

import java.util.Objects;

/**
 * Base class for all entities in the application
 * @param <ID> the type of the entity's unique identifier
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
