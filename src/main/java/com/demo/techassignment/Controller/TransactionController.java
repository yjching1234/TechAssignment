package com.demo.techassignment.Controller;

import com.demo.techassignment.DTO.ManageTransactionDTO;
import com.demo.techassignment.DTO.TransactionDTO;
import com.demo.techassignment.DTO.TrnHistoryDTO;
import com.demo.techassignment.Service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {


    private final TransactionService transactionService;


    public TransactionController(@Lazy TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/make")
    public ResponseEntity<Object> Transaction(@RequestBody TransactionDTO transactionDTO){
        try{
            Map<String,Object> response = transactionService.makeTransaction(transactionDTO);
            if (response.containsKey("errors")){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/trnApproval")
    public ResponseEntity<Object> transactionApproval(@RequestBody @Valid ManageTransactionDTO manageTransactionDTO){
        try {
            Map<String,Object> response = transactionService.manageTransaction(manageTransactionDTO);

            if (response.containsKey("errors")){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("errors",e.getMessage()));
        }
    }

    @PostMapping("/getTrnHis")
    public  ResponseEntity<Object> getTransactionHistory(@RequestBody @Valid TrnHistoryDTO trnHistoryDTO){
        try {
            Map<String,Object> response = transactionService.getTransactionHistory(trnHistoryDTO);

            if (response.containsKey("errors")){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            return ResponseEntity.ok(response);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("errors",e.getMessage()));
        }
    }
}
