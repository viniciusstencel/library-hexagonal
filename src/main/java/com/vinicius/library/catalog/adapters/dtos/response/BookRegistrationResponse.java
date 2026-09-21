package com.vinicius.library.catalog.adapters.dtos.response;

import java.util.UUID;

public class BookRegistrationResponse {

    private UUID id;

    private String title;


    public BookRegistrationResponse() {
    }

    public BookRegistrationResponse(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
