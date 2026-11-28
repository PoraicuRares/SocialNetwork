package com.lab.repository.db;

import com.lab.domain.FriendshipRequest;
import com.lab.domain.FriendshipStatus;
import com.lab.validator.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class FriendshipRequestDBRepository extends AbstractDBRepository<Long, FriendshipRequest> {
    public FriendshipRequestDBRepository(Validator<FriendshipRequest> validator, String url, String user, String password) {
        super(validator, url, user, password);
    }

    @Override
    protected String selectAllSql() {
        return "SELECT * FROM friend_requests";
    }

    @Override
    protected String selectOneSql() {
        return "SELECT * FROM friend_requests WHERE id = ?";
    }

    @Override
    protected String deleteSql() {
        return "DELETE FROM friend_requests WHERE id = ?";
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO friend_requests(from_id,to_id , status,date) VALUES(? ,?,?,?)";
    }

    @Override
    protected String updateSql() {
        return "UPDATE friend_requests SET status = ? WHERE id = ?";
    }

    @Override
    protected FriendshipRequest extractEntity(ResultSet resultSet) throws SQLException {
        return new FriendshipRequest(
                resultSet.getLong("id"),
                resultSet.getLong("from_id"),
                resultSet.getLong("to_id"),
                FriendshipStatus.valueOf(resultSet.getString("status")),
                resultSet.getObject("date" , LocalDateTime.class)
        );
    }

    @Override
    protected void setIdParameters(PreparedStatement preparedStatement, Long id) throws SQLException {
        preparedStatement.setLong(1,id);
    }

    @Override
    protected void setInsertParameters(PreparedStatement preparedStatement, FriendshipRequest entity) throws SQLException {
        preparedStatement.setLong(1,entity.getFromId());
        preparedStatement.setLong(2,entity.getToId());
        preparedStatement.setString(3,entity.getStatus().toString());
        preparedStatement.setObject(4,entity.getDate());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement preparedStatement, FriendshipRequest entity) throws SQLException {
        preparedStatement.setString(1 ,entity.getStatus().toString());
        preparedStatement.setLong(2,entity.getId());
    }
}
