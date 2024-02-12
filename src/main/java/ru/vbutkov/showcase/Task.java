package ru.vbutkov.showcase;

import java.util.UUID;

public record Task(UUID id, String details, boolean completed, UUID userId) {
    public Task(String details, UUID id) {
        this(UUID.randomUUID(), details, false, id);
    }
}
