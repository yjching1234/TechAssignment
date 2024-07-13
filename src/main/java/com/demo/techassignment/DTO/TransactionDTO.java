package com.demo.techassignment.DTO;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TransactionDTO {

//    private String accountFrom;

    private String accountTo;
    private Double amount;
    private String remarks;
    private String description;
    private Integer transactionType;

}
