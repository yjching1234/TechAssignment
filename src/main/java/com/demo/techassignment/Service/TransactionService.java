package com.demo.techassignment.Service;

import com.demo.techassignment.DTO.TransactionDTO;

import java.util.Map;

public interface TransactionService {
    Map<String, String> makeTransaction(TransactionDTO transactionDTO);
}
