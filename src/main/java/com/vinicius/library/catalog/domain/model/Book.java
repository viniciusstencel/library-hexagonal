package com.vinicius.library.catalog.domain.model;

import java.util.UUID;

public class Book {

    private UUID id;
    private String title;
    private Integer yearOfPublishment;

    public Book(UUID id, String title, Integer yearOfPublishment) {
        this.id = id;
        this.title = title;
        this.yearOfPublishment = yearOfPublishment;
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

    public Integer getYearOfPublishment() {
        return yearOfPublishment;
    }

    public void setYearOfPublishment(Integer yearOfPublishment) {
        this.yearOfPublishment = yearOfPublishment;
    }
}
