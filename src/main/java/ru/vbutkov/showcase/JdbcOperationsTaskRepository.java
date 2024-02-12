package ru.vbutkov.showcase;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcOperationsTaskRepository implements TaskRepository, RowMapper<Task> {
    JdbcOperations jdbcOperation;

    public JdbcOperationsTaskRepository(JdbcOperations jdbcOperation) {
        this.jdbcOperation = jdbcOperation;
    }

    @Override
    public List<Task> findAll() {
        return this.jdbcOperation.query("select * from t_task", this);
    }

    @Override
    public void save(Task task) {
        this.jdbcOperation.update("""
                insert into t_task(id, details, completed, user_id) values (?, ?, ?, ?);
                """, task.id(), task.details(), task.completed(), task.userId());
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return this.jdbcOperation.query("select * from t_task where id = ?", new Object[]{id}, this)
                .stream().findFirst();
    }

    @Override
    public List<Task> findByUserId(UUID id) {
        return this.jdbcOperation.query("select * from t_task where user_id = ?", this, id);
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Task(
                rs.getObject("id", UUID.class),
                rs.getString("details"),
                rs.getBoolean("completed"),
                rs.getObject("user_id", UUID.class)
        );
    }
}
