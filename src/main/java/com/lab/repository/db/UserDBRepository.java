package com.lab.repository.db;

import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.PagingRepository;
import com.lab.util.paging.Page;
import com.lab.util.paging.Pageable;
import com.lab.validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database repository implementation for {@link User} entities
 * Extends {@link AbstractDBRepository} and prepares the environment for JDBC persistence
 */
public class UserDBRepository extends AbstractDBRepository<Long , User>  implements PagingRepository<Long,User> {

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

    private int count(Connection connection){
        String sql  = "SELECT COUNT(*) AS count FROM users";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return (resultSet.next()) ? resultSet.getInt("count") : 0;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error at counting the users " + e.getMessage() , e);
        }
    }

    private List<User> findAllOnPage(Connection connection, Pageable pageable){
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id LIMIT ? OFFSET ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setLong(1 , pageable.pageSize());
            preparedStatement.setLong(2, (long) pageable.pageSize() * pageable.pageNumber());
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    User user = new User(
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name")
                    );
                    user.setId(resultSet.getLong("id"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error at creating the user page " + e.getMessage() , e);
        }
        return users;
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        try(Connection connection = getConnection() ) {
            int totalNumberOfUsers = count(connection);
            List<User> usersOnPage = (totalNumberOfUsers > 0) ? findAllOnPage(connection , pageable) : new ArrayList<>();
            return new Page<>(usersOnPage ,totalNumberOfUsers);
        } catch (SQLException e) {
            throw new RepositoryException("Error at creating the user page " + e.getMessage() , e);
        }
    }
}
