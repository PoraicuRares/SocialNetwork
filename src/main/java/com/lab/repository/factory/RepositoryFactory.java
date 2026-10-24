package com.lab.repository.factory;

import com.lab.domain.User;
import com.lab.repository.InMemoryRepository;
import com.lab.repository.Repository;
import com.lab.repository.file.UserFileRepository;
import com.lab.validator.Validator;

import java.util.Properties;

public class RepositoryFactory {
    private final Properties properties  = new Properties();

    public RepositoryFactory(){

    }

    public Repository<Long,User> createUserRepository(Validator<User> validator){
        String value = properties.getProperty("repository.type");
        if(value == null){
            throw new IllegalArgumentException("The key repository.type is missing from the properties file");
        }
        RepositoryType type = RepositoryType.valueOf(value.trim().toUpperCase());
        switch (type){

            case MEMORY :
                return new InMemoryRepository<>(validator);

            case FILE:
                String file = properties.getProperty("repository.file.users");
                if(file == null) throw new IllegalArgumentException("User file path is missing");
                return new UserFileRepository(validator , file);

            case DATABASE:
                String dbUrl
        }
    }
}
