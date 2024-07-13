package com.demo.techassignment.Controller;

import com.demo.techassignment.DTO.TransactionDTO;
import com.demo.techassignment.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {


    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/make")
    public ResponseEntity<Object> Transaction(@RequestBody TransactionDTO transactionDTO){
        try{
            return new ResponseEntity<Object>(transactionService.makeTransaction(transactionDTO), HttpStatus.ACCEPTED);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<String> Withdraw(@RequestBody TransactionDTO transactionDTO){
        return null;
    }

    public ResponseEntity<String> Transfer(@RequestBody TransactionDTO transactionDTO){
        return null;
    }
}
