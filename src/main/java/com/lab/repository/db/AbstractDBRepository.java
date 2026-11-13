package com.lab.repository.db;

import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.Repository;
import com.lab.validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for JDBC Database repositories
 * Stores connection credentials and throws {@link UnsupportedOperationException}
 * for all CRUD methods as preparation for the next laboratory requirement
 * @param <ID> the type of the entity's identifier
 * @param <E> the type of the entity, which must extend {@link Entity}
 */
public abstract class AbstractDBRepository<ID , E extends Entity<ID>> implements Repository<ID   , E> {
    protected final Validator<E> validator;
    private final String url;
    private final String user;
    private final String password;

    public AbstractDBRepository(Validator<E> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    protected abstract String selectAllSql();
    protected abstract String selectOneSql();
    protected abstract String deleteSql();
    protected abstract String insertSql();
    protected abstract String updateSql();

    protected abstract E extractEntity(ResultSet resultSet) throws SQLException;
    protected abstract void setIdParameters(PreparedStatement preparedStatement ,ID id) throws SQLException ;
    protected abstract void setInsertParameters(PreparedStatement preparedStatement ,E entity) throws SQLException;
    protected abstract void setUpdateParameters(PreparedStatement preparedStatement , E entity) throws SQLException;

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url , user , password);
    }

    protected void readGeneratedIds(ResultSet resultSet,E entity) throws SQLException{

    }

    private static void requireNonNull(String what , Object object){
        if(object == null){
            throw new IllegalArgumentException(what + " must not be null");
        }
    }

    @Override
    public Iterable<E> findAll() {
        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(selectAllSql());
            ResultSet resultSet = statement.executeQuery()
            ){
            List<E> entities = new ArrayList<>();
            while (resultSet.next()){
                entities.add(extractEntity(resultSet));
            }
            return entities;
        } catch (SQLException e) {
            throw new RepositoryException("Error while reading from the db " + e.getMessage(), e);
        }
    }


    @Override
    public Optional<E> findOne(ID id) {
        requireNonNull("Id" , id);
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectOneSql())) {
            setIdParameters(preparedStatement,id);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return resultSet.next() ? Optional.of(extractEntity(resultSet)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error while reading from the db " + e.getMessage() , e);
        }
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> existing = findOne(id);
        if(existing.isEmpty()){
            return existing;
        }
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement =connection.prepareStatement(deleteSql())) {
            setIdParameters(preparedStatement , id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error while deleting from the db " + e.getMessage() , e);
        }
        return existing;
    }

    @Override
    public Optional<E> save(E entity) {
        requireNonNull("Entity" , entity);
        validator.validate(entity);
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql() , Statement.RETURN_GENERATED_KEYS)) {
            setInsertParameters(preparedStatement,entity);
            preparedStatement.executeUpdate();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                if(resultSet.next()){
                    readGeneratedIds(resultSet,entity);
                }
            }
        } catch (SQLException e) {
            if("23505".equals(e.getSQLState())){
                return Optional.of(entity);
            }
            throw new RepositoryException("Error while inserting in the db "+ e.getMessage() , e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<E> update(E entity) {
        requireNonNull("Entity"  , entity);
        requireNonNull("Id " , entity.getId());
        validator.validate(entity);
        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateSql())) {
            setUpdateParameters(preparedStatement, entity);
            int changedRows = preparedStatement.executeUpdate();
            return changedRows != 0 ? Optional.empty() : Optional.of(entity);
        } catch (SQLException e) {
            throw new RepositoryException("Error while updating the db "+e.getMessage() , e);
        }
    }
}
