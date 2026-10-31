package com.lab.repository.factory;

import com.lab.domain.Entity;
import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.InMemoryRepository;
import com.lab.repository.Repository;
import com.lab.repository.db.FriendshipDBRepository;
import com.lab.repository.db.UserDBRepository;
import com.lab.repository.file.FileRepository;import com.lab.repository.file.LineConverters;import com.lab.validator.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

/**
 * Factory class responsible for instantiating repository implementations
 * based on the configuration file settings.
 */
public class RepositoryFactory {
    private static RepositoryFactory instance = null;

    private final Properties properties;
    private final RepositoryType repositoryType;

    private final Map<RepositoryType, Function<Validator<User> , Repository<Long , User>>> userRepositoryCreators;
    private final Map<RepositoryType , Function<Validator<Friendship> , Repository<Tuple<Long,Long> , Friendship> >> friendshipRepositoryCreators;

    private RepositoryFactory(String configFile){
        properties = loadProperties(configFile);
        repositoryType = readRepositoryType();
        userRepositoryCreators = buildUserRepositoryCreators();
        friendshipRepositoryCreators = buildFriendshipRepositoryCreators();
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

    private RepositoryType readRepositoryType(){
        String value = properties.getProperty("repository.type");
        if(value == null){
            throw new IllegalArgumentException("The key repository.type is missing from the properties file");
        }
        return RepositoryType.valueOf(value.trim().toUpperCase());
    }

    private Map<RepositoryType, Function<Validator<User> , Repository<Long , User>>> buildUserRepositoryCreators(){
        return Map.of(
                RepositoryType.MEMORY ,InMemoryRepository::new,
                RepositoryType.FILE , validator -> new FileRepository<>(validator,
                        getRequiredProperty("repository.file.users") ,
                        LineConverters::parseUser , LineConverters::formatUser),
                RepositoryType.DATABASE , validator -> new UserDBRepository(validator,
                        getRequiredProperty("db.url") ,getRequiredProperty("db.user") , getRequiredProperty("db.password"))

        );
    }

    private Map<RepositoryType , Function<Validator<Friendship> , Repository<Tuple<Long,Long> , Friendship>>> buildFriendshipRepositoryCreators(){
        return Map.of(
                RepositoryType.MEMORY , InMemoryRepository::new ,
                RepositoryType.FILE , validator-> new FileRepository<>(validator,
                        getRequiredProperty("repository.file.friendships"),
                        LineConverters::parseFriendship , LineConverters::formatFriendship),
                RepositoryType.DATABASE , validator -> new FriendshipDBRepository(validator,
                        getRequiredProperty("db.url") , getRequiredProperty("db.user") , getRequiredProperty("db.password"))
        );
    }

    private <ID,E extends Entity<ID>> Repository<ID ,E> create(
            Map<RepositoryType , Function<Validator<E> , Repository<ID , E>>> creators,
            Validator<E> validator){
        return Optional.ofNullable(creators.get(repositoryType))
                .map(creator -> creator.apply(validator))
                .orElseThrow(() -> new RepositoryException("Unknown repository type provided"));
    }

    public Repository<Long ,User> createUserRepository(Validator<User> validator){
        return create(userRepositoryCreators ,validator);
    }

    public Repository<Tuple<Long,Long> , Friendship> createFriendshipRepository(Validator<Friendship> validator){
        return create(friendshipRepositoryCreators , validator);
    }

}
