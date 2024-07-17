package com.demo.techassignment.Service;

import com.demo.techassignment.DTO.ManageAccountDTO;
import com.demo.techassignment.DTO.ManageTransactionDTO;
import com.demo.techassignment.DTO.TransactionDTO;
import com.demo.techassignment.DTO.TrnHistoryDTO;

import java.util.Map;

public interface TransactionService {
    Map<String, Object> makeTransaction(TransactionDTO transactionDTO) throws Exception;

    Map<String, Object> manageTransaction(ManageTransactionDTO manageTransactionDTO);

    Map<String, Object> getTransactionHistory(TrnHistoryDTO trnHistoryDTO) throws Exception;

    Map<String, Object> manageAcc(ManageAccountDTO manageAccountDTO) throws Exception;
}
