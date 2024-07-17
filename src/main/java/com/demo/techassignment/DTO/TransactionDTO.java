package com.demo.techassignment.DTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDTO {

//    private String accountFrom;
    private String accountTo;
    @Pattern(regexp = "^[\\d]{1,}$", message = "Invalid Amount")
    private Double amount;
    private String remarks;
    @NotEmpty
    private String description;
    @NotEmpty
    @Pattern(regexp = "^[\\d]{1,}$", message = "Invalid Amount")
    private Integer transactionType;

}
