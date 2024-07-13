package com.demo.techassignment.DTO;

import com.demo.techassignment.Model.Enum.TrnType;
import jakarta.persistence.Column;
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

    private Integer transactionType;

}
