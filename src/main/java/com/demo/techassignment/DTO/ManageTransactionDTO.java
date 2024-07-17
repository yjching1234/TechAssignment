package com.demo.techassignment.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageTransactionDTO {
    @NotEmpty
    private String trnId;
    @NotNull
    private Integer trnStatus;
    @NotEmpty
    private String remarks;
}
