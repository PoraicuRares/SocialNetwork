package com.lab.repository.db;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Skeleton database repository implementation for {@link Friendship} entities
 * Extends {@link AbstractDBRepository} and prepares the environment for JDBC persistence
 */
public class FriendshipDBRepository extends AbstractDBRepository<Tuple<Long,Long> , Friendship> {
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
        friendship.setDate(resultSet.getObject("friends_from" , LocalDateTime.class));
        return friendship;
    }

    @Override
    protected void setIdParameters(PreparedStatement preparedStatement, Tuple<Long, Long> id) throws SQLException {
        preparedStatement.setLong(1 , id.getLeft());
        preparedStatement.setLong(2, id.getRight());
    }

    @Override
    protected void setInsertParameters(PreparedStatement preparedStatement, Friendship entity) throws SQLException {
        preparedStatement.setLong(1 , entity.getId().getLeft());
        preparedStatement.setLong(2, entity.getId().getRight());
        preparedStatement.setObject(3, entity.getDate());

    }

    @Override
    protected void setUpdateParameters(PreparedStatement preparedStatement, Friendship entity) throws SQLException {
        preparedStatement.setObject(1 , entity.getDate());
        preparedStatement.setLong(2 , entity.getId().getLeft());
        preparedStatement.setLong(3 , entity.getId().getRight());
    }
}
