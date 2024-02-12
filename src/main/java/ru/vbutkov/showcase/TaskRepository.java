package ru.vbutkov.showcase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    List<Task> findAll();

    void save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findByUserId(UUID id);
}
