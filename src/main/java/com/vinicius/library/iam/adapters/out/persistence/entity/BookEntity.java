package com.vinicius.library.iam.adapters.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "book")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "year_of_publishment")
    private Integer yearOfPublishment;


    public BookEntity() {
    }

    public BookEntity(UUID uuid, String name, Integer yearOfPublishment) {
        this.uuid = uuid;
        this.name = name;
        this.yearOfPublishment = yearOfPublishment;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYearOfPublishment() {
        return yearOfPublishment;
    }

    public void setYearOfPublishment(Integer yearOfPublishment) {
        this.yearOfPublishment = yearOfPublishment;
    }
}
