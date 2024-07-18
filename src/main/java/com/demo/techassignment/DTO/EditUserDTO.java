package com.demo.techassignment.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class EditUserDTO {

    private String username;
    @NotEmpty
    @Size(min = 5, max = 30)
    private String name;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Pattern(regexp = "^\\+?\\d{1,4}?[-]?(\\(?\\d{1,4}?\\)?[-.\\s]?){1,5}\\d{1,4}$", message = "Invalid Contact No")
    private String contact;
    private String pass;
    private Integer role;
    private Integer accStatus;
}
