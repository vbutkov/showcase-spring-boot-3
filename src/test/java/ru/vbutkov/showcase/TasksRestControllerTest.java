package ru.vbutkov.showcase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksRestControllerTest {
    @Mock
    TaskRepository taskRepository;
    @Mock
    MessageSource messageSource;
    @InjectMocks
    TasksRestController controller;

    @Test
    @DisplayName("GET /api/tasks/ Get list tasks")
    void handleGetAllTasks_ReturnsValidResponseEntity() {
        //given
        List<Task> tasks = List.of(
                new Task(UUID.randomUUID(), "Первая задача", false),
                new Task(UUID.randomUUID(), "Вторая задача", false)
        );
        doReturn(tasks).when(this.taskRepository).findAll();

        //when
        ResponseEntity<List<Task>> responseEntity = this.controller.handleGetAllTasks();

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(tasks, responseEntity.getBody());

    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() {
        //given
        String details = "Третья задача";

        //when
        ResponseEntity<?> responseEntity = this.controller.handleCreateNewTask(
                new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"),
                Locale.ENGLISH);

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        if (responseEntity.getBody() instanceof Task task) {
            assertNotNull(task.id());
            assertEquals(details, task.details());
            assertFalse(task.completed());

            assertEquals(
                    URI.create("http://localhost:8080/api/tasks/" + task.id()),
                    responseEntity.getHeaders().getLocation()
            );

            verify(this.taskRepository).save(task);
        } else {
            assertInstanceOf(Task.class, responseEntity.getBody());
        }

        verifyNoMoreInteractions(this.taskRepository);
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() {
        //given
        String details = "    ";
        Locale locale = Locale.CANADA;
        String errorMsg = "Details is empty";

        doReturn(errorMsg).when(this.messageSource).getMessage(
                "tasks.create.details.errors.not_set",
                new Object[0],
                locale
        );

        //when
        ResponseEntity<?> responseEntity = this.controller.handleCreateNewTask(
                new NewTaskPayload(details),
                UriComponentsBuilder.fromUriString("http://localhost:8080"),
                locale
        );

        //then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorsPresentation(List.of(errorMsg)), responseEntity.getBody());

        verifyNoInteractions(taskRepository);
    }
}