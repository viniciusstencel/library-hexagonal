package com.vinicius.library.catalog.adapters.in.web;

import com.vinicius.library.catalog.adapters.dtos.request.BookRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalog")
public class BookController {


    @PostMapping("/books")
    public ResponseEntity<?> bookRegistration(@RequestBody BookRegistrationRequest request){
        return null;
    }



}
