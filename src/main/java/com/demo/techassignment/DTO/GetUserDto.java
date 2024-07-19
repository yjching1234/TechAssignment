package com.demo.techassignment.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class GetUserDto {
    @NotEmpty
    private String username;
}
