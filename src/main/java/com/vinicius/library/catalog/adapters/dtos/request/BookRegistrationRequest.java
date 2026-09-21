package com.vinicius.library.catalog.adapters.dtos.request;

import jakarta.validation.constraints.NotBlank;

public class BookRegistrationRequest {


    private String isbn;

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    private String publisher;

    private Integer publishedYear;

    public BookRegistrationRequest() {
    }

    public BookRegistrationRequest(String isbn, String title, String author, String publisher, Integer publishedYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishedYear = publishedYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }
}
