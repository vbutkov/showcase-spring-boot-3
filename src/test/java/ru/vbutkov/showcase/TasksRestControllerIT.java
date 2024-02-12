package ru.vbutkov.showcase;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/sql/tasks_rest_controller/test_data.sql")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = get("/api/tasks")
                .with(httpBasic("user1", "password1"));
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
                .with(httpBasic("user2", "password2"))
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
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = post("/api/tasks")
                .with(httpBasic("user1", "password1"))
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
                                                            "errors":["Не указано описание задачи"]
                                                            }
                                """, true)
                );
    }

}