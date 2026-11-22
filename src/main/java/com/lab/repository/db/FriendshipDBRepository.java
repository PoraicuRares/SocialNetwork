package com.lab.repository.db;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.repository.FriendshipPagingRepository;
import com.lab.util.paging.Page;
import com.lab.util.paging.Pageable;
import com.lab.validator.Validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database repository implementation for {@link Friendship} entities
 * Extends {@link AbstractDBRepository} and prepares the environment for JDBC persistence
 */
public class FriendshipDBRepository extends AbstractDBRepository<Tuple<Long, Long>, Friendship> implements FriendshipPagingRepository {
    public FriendshipDBRepository(Validator<Friendship> validator, String url, String user, String password) {
        super(validator, url, user, password);
    }

    @Override
    protected String selectAllSql() {
        return "SELECT * FROM friendships";
    }

    @Override
    protected String selectOneSql() {
        return "SELECT * FROM friendships WHERE id1 = ? AND id2 = ? ";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM friendships WHERE id1 = ? and id2 = ?";
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO friendships(id1 ,id2 , friends_from) VALUES(?,?,?) ";
    }

    @Override
    protected String updateSql() {
        return "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ? ";
    }

    @Override
    protected Friendship extractEntity(ResultSet resultSet) throws SQLException {
        Friendship friendship = new Friendship(
                resultSet.getLong("id1"),
                resultSet.getLong("id2")
        );
        friendship.setDate(resultSet.getObject("friends_from", LocalDateTime.class));
        return friendship;
    }

    @Override
    protected void setIdParameters(PreparedStatement preparedStatement, Tuple<Long, Long> id) throws SQLException {
        preparedStatement.setLong(1, id.getLeft());
        preparedStatement.setLong(2, id.getRight());
    }

    @Override
    protected void setInsertParameters(PreparedStatement preparedStatement, Friendship entity) throws SQLException {
        preparedStatement.setLong(1, entity.getId().getLeft());
        preparedStatement.setLong(2, entity.getId().getRight());
        preparedStatement.setObject(3, entity.getDate());

    }

    @Override
    protected void setUpdateParameters(PreparedStatement preparedStatement, Friendship entity) throws SQLException {
        preparedStatement.setObject(1, entity.getDate());
        preparedStatement.setLong(2, entity.getId().getLeft());
        preparedStatement.setLong(3, entity.getId().getRight());
    }

    private int count(Connection connection, Long userId) {
        String sql = "SELECT COUNT(*) AS total FROM friendships f WHERE f.id1 = ? OR f.id2 = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return (resultSet.next()) ? resultSet.getInt("total") : 0;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error at counting the friendships " + e.getMessage(), e);
        }
    }

    private List<User> findFriendsOnPage(Connection connection, Long userId, Pageable pageable) {
        String sql = "SELECT u.id , u.first_name ,u.last_name FROM friendships f INNER JOIN users u ON (f.id1 = ?  AND u.id = f.id2) OR (f.id2 = ? AND u.id = f.id1) ORDER BY u.id LIMIT ? OFFSET ?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, userId);
            preparedStatement.setLong(3, pageable.pageSize());
            preparedStatement.setLong(4, (long) pageable.pageNumber() * pageable.pageSize());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User u = new User(
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name")
                    );
                    u.setId(resultSet.getLong("id"));
                    users.add(u);
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error at finding the friends on a page " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public Page<User> findFriendsOnPage(Long userId, Pageable pageable) {
        try (Connection connection = getConnection()) {
            int total = count(connection, userId);
            List<User> users = findFriendsOnPage(connection, userId, pageable);
            return new Page<>(users, total);
        } catch (SQLException e) {
            throw new RepositoryException("Error at finding the friends on a page " + e.getMessage(), e);
        }
    }
}
