package com.maxilect.idfactory.controller;


import com.maxilect.idfactory.model.IdValue;
import com.maxilect.idfactory.service.IdGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = IdGeneratorController.class)
class IdGeneratorControllerTest {

    private final String ROUTE_URL = "/id";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IdGenerator idGenerator;

    @Test
    void getRequestShouldReturnId() throws Exception {

        IdValue idValue = new IdValue(5);

        Mockito.when(idGenerator.generateUniqueId()).thenReturn(idValue);
        this.mvc.perform(get(ROUTE_URL).contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is((int)idValue.getId())));
    }

    @Test
    void exceptionShouldReturn500code() throws Exception {
        Mockito.when(idGenerator.generateUniqueId()).thenThrow(RuntimeException.class);
        this.mvc.perform(get(ROUTE_URL).contentType(MediaType.ALL))
                .andExpect(status().isInternalServerError());

    }

}