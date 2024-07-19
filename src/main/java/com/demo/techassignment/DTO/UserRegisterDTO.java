package com.demo.techassignment.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserRegisterDTO {
    @NotEmpty
    @Size(min = 5, max = 15)
    @Pattern(regexp = "^[\\w\\W]{5,15}$", message = "Username should not have space")
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
    @NotEmpty
    @Pattern(regexp = "^\\d{6}(-?\\d{2})(-?\\d{4})$", message = "Invalid Id no")
    private String idNo;
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character (e.g., @, #, $, %, &, etc.)")
    private String pass;
}
