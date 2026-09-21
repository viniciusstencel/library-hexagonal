package com.vinicius.library.catalog.application.ports.in;

public interface BookRegistrationInputPort {
    BookRegistrationResponseDTO register(BookRegistrationRequestDTO request);
}
