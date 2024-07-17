package com.demo.techassignment.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ManageAccountDTO {
    @NotEmpty
    private String accNo;
    @NotEmpty
    private String remarks;
    @NotNull
    private Integer accStatus;
}
