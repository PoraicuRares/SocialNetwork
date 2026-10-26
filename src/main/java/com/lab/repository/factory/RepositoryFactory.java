package com.lab.repository.factory;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.InMemoryRepository;
import com.lab.repository.Repository;
import com.lab.repository.db.FriendshipDBRepository;
import com.lab.repository.db.UserDBRepository;
import com.lab.repository.file.FriendshipFileRepository;
import com.lab.repository.file.UserFileRepository;
import com.lab.validator.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Factory class responsible for instantiating repository implementations
 * based on the configuration file settings.
 */
public class RepositoryFactory {
    private static RepositoryFactory instance = null;

    private final Properties properties;
    private final RepositoryType repositoryType;

    private RepositoryFactory(String configFile){
        properties = loadProperties(configFile);
        String value = properties.getProperty("repository.type");
        if(value == null){
            throw new IllegalArgumentException("The key repository.type is missing from the properties file");
        }
        repositoryType = RepositoryType.valueOf(value.trim().toUpperCase());
    }

    /**
     * Returns the Singleton RepositoryFactory instance
     * @param configFile configuration file name
     * @return the instance of RepositoryFactory
     */
    public static RepositoryFactory getInstance(String configFile){
        if(instance == null)
            instance = new RepositoryFactory(configFile);
        return instance;
    }

    private Properties loadProperties(String resource){
        Properties prop = new Properties();
        try(InputStream in = getClass().getClassLoader().getResourceAsStream(resource)){
            if(in == null){
                throw new RepositoryException("Config file not found in resources: " + resource);
            }
            prop.load(in);
        }catch (IOException e){
            throw new RepositoryException("Error while reading the config file" , e);
        }
        return prop;
    }

    private String getRequiredProperty(String key){
        String value = properties.getProperty(key);
        if(value == null){
            throw new IllegalArgumentException("The required property '" + key + "' is missing");
        }
        return value;
    }

    /**
     * Creates a user repository based on the configured persistence type.
     *
     * @param validator the validator used for user entities
     * @return a concrete implementation of {@link Repository} for users
     * @throws RepositoryException if the repository type is unknown or configuration fails
     */
    public Repository<Long,User> createUserRepository(Validator<User> validator){
        switch (repositoryType){
            case MEMORY :
                return new InMemoryRepository<>(validator);

            case FILE:
                String file = getRequiredProperty("repository.file.users");
                return new UserFileRepository(validator , file);

            case DATABASE:
                String dbUrl = getRequiredProperty("db.url");
                String dbUser = getRequiredProperty("db.user");
                String dbPassword = getRequiredProperty("db.password");
                return new UserDBRepository(validator,dbUrl,dbUser,dbPassword);

            default:
                throw new RepositoryException("Unknown repository type provided");
        }
    }

    /**
     * Creates a friendship repository based on the configured persistence type.
     *
     * @param validator the validator used for friendship entities
     * @return a concrete implementation of {@link Repository} for friendships
     * @throws RepositoryException if the repository type is unknown or configuration fails
     */
    public Repository<Tuple<Long,Long>, Friendship> createFriendshipRepository(Validator<Friendship> validator){
        switch (repositoryType){
            case MEMORY :
                return new InMemoryRepository<>(validator);

            case FILE:
                String file = getRequiredProperty("repository.file.friendships");
                return new FriendshipFileRepository(validator , file);

            case DATABASE:
                String dbUrl = getRequiredProperty("db.url");
                String dbUser = getRequiredProperty("db.user");
                String dbPassword = getRequiredProperty("db.password");
                return new FriendshipDBRepository(validator,dbUrl,dbUser,dbPassword);

            default:
                throw new RepositoryException("Unknown repository type provided");
        }
    }
}
