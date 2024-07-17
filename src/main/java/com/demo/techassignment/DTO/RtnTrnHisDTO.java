package com.demo.techassignment.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RtnTrnHisDTO {
    private String username;
    private String trnId;
    private String transactionDateTime;
    private String accountFrom;
    private String accountTo;
    private Double amount;
    private String description;
    private String remarks;
    private String trnStatus; // This will hold the formatted status
    private String trnType;
}
