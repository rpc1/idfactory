package com.maxilect.idfactory.controller;

import com.maxilect.idfactory.model.IdValue;
import com.maxilect.idfactory.service.IdGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/id")
public class IdGeneratorController {

    private final IdGenerator idGenerator;

    public IdGeneratorController(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public IdValue getUniqueId() {
        return idGenerator.generateUniqueId();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleException(Exception e) {
        LoggerFactory.getLogger(IdGeneratorController.class).error(e.getMessage());
    }
}

