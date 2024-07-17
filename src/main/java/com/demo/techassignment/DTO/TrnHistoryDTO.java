package com.demo.techassignment.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class TrnHistoryDTO {
    private Integer userId;
    private String trnId;
    private Integer trnStatus;
    private Integer trnType;
    private String dateFrom;
    private String dateTo;
    @NotEmpty
    @Pattern(regexp = "^[AD]{1}$", message = "A - ascending, B - descending")
    private String sort;
    @NotNull
    @Min(0)
    private Integer page;
}
