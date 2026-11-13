package com.lab.repository.db;

import com.lab.domain.User;
import com.lab.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Skeleton database repository implementation for {@link User} entities
 * Extends {@link AbstractDBRepository} and prepares the environment for JDBC persistence
 */
public class UserDBRepository extends AbstractDBRepository<Long , User> {

    public UserDBRepository(Validator<User> validator, String url, String user, String password) {
        super(validator, url, user, password);
    }

    @Override
    protected String selectAllSql() {
        return "SELECT * FROM users";
    }

    @Override
    protected String selectOneSql() {
        return "SELECT * FROM users WHERE id = ?";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM users WHERE id = ?";
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO users(first_name,last_name) VALUES(?,?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE users SET first_name = ? ,last_name = ? WHERE id = ?";
    }

    @Override
    protected User extractEntity(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name")
        );
    }

    @Override
    protected void setIdParameters(PreparedStatement preparedStatement, Long id) throws SQLException {
        preparedStatement.setLong(1 , id);
    }

    @Override
    protected void setInsertParameters(PreparedStatement preparedStatement, User entity) throws SQLException {
        preparedStatement.setString(1 , entity.getFirstName());
        preparedStatement.setString(2 ,entity.getLastName());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement preparedStatement, User entity) throws SQLException {
        preparedStatement.setString(1 , entity.getFirstName());
        preparedStatement.setString(2 ,entity.getLastName());
        preparedStatement.setLong(3, entity.getId());
    }

    @Override
    protected void readGeneratedIds(ResultSet resultSet, User entity) throws SQLException {
        entity.setId(resultSet.getLong("id"));
    }
}
