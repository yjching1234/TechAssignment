package com.demo.techassignment.Service;

import com.demo.techassignment.DTO.TransactionDTO;
import com.demo.techassignment.Model.Account;
import com.demo.techassignment.Model.Enum.TrnType;
import com.demo.techassignment.Repository.AccountRepository;
import com.demo.techassignment.Repository.TransactionReposiotry;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionServiceImp implements TransactionService{

    private final TransactionReposiotry transactionReposiotry;
    private final AccountRepository accountRepository;

    public TransactionServiceImp(TransactionReposiotry transactionReposiotry, AccountRepository accountRepository) {
        this.transactionReposiotry = transactionReposiotry;
        this.accountRepository = accountRepository;
    }

    @Override
    public Map<String, String> makeTransaction(TransactionDTO transactionDTO) {
        Map<String,String> errors = new HashMap<>();

        switch (TrnType.fromValue(transactionDTO.getTransactionType())){
            case TrnType.TRANSFER -> {
                if(transactionDTO.getAccountTo().isEmpty()){
                    errors.put("accountTo","The account number is empty.");
                    break;
                }

                if(accountRepository.findByAccountNo(transactionDTO.getAccountTo()).isEmpty()){
                    errors.put("accountTo","The account number not exist.");
                    break;
                }else{
                    Account accTo = accountRepository.findByAccountNo(transactionDTO.getAccountTo()).get();

                }
                break;
            }

        }




        if(transactionDTO.getAmount() <= 0){
            errors.put("amount","Transaction amount cannot less then equal 0");
        }

        if(transactionDTO.getRemarks().isEmpty()){
            errors.put("remarks","Remarks cannot be empty");
        }

        if(!errors.isEmpty()){
            return errors;
        }


        return Collections.singletonMap("msg", "Success");
    }
}
