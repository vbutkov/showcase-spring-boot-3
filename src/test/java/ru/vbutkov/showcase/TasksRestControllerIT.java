package ru.vbutkov.showcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InMemTaskRepository taskRepository;

    @AfterEach
    void tearDown() {
        this.taskRepository.getTasks().clear();
    }

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = get("/api/tasks");
        this.taskRepository.getTasks()
                .addAll(List.of(
                        new Task(UUID.fromString("3468b72c-c682-11ee-bb8c-38d57ae4482d"),
                                "Первая задача",
                                false),
                        new Task(UUID.fromString("358a4d64-c682-11ee-bf3e-38d57ae4482d"),
                                "Вторая задача",
                                true)
                ));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                           "id": "3468b72c-c682-11ee-bb8c-38d57ae4482d",
                                           "details": "Первая задача",
                                           "completed": false                                              
                                    },
                                    {
                                           "id": "358a4d64-c682-11ee-bf3e-38d57ae4482d",
                                           "details": "Вторая задача",
                                           "completed": true
                                    }
                                ]
                                """)
                );


    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                        {
                                            "details" : "Третья задача"
                                        }
                        """);

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                                            {
                                                            "details": "Третья задача",
                                                            "completed": false
                                                            }
                                """),
                        jsonPath("$.id").exists()
                );

        assertEquals(1, this.taskRepository.getTasks().size());
        Task task = this.taskRepository.getTasks().get(0);
        assertNotNull(task.id());
        assertEquals("Третья задача", task.details());
        assertFalse(this.taskRepository.getTasks().get(0).completed());
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .content("""
                                        {
                                            "details" : "   "
                                        }
                        """);

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                                            {
                                                            "errors":["Task detail must be set"]
                                                            }
                                """, true)
                );

        assertTrue(this.taskRepository.getTasks().isEmpty());
    }

}