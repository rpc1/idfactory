package com.maxilect.idfactory.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdValue {
    @JsonProperty("id")
    private final long id;

    public IdValue(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
