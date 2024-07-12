package com.demo.techassignment.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserRegisterDTO {
    private String username;

    private String name;

    private String email;

    private String contact;

    private String pass;
}
