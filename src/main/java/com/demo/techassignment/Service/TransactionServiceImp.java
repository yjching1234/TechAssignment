package com.demo.techassignment.Service;

import com.demo.techassignment.DTO.TransactionDTO;
import com.demo.techassignment.Model.Account;
import com.demo.techassignment.Model.Enum.AccStatus;
import com.demo.techassignment.Model.Enum.TrnStatus;
import com.demo.techassignment.Model.Enum.TrnType;
import com.demo.techassignment.Model.Transaction;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.AccountRepository;
import com.demo.techassignment.Repository.TransactionReposiotry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TransactionServiceImp implements TransactionService{

    private final TransactionReposiotry transactionReposiotry;
    private final AccountRepository accountRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private GlobalService globalService;

    public TransactionServiceImp(TransactionReposiotry transactionReposiotry, AccountRepository accountRepository) {
        this.transactionReposiotry = transactionReposiotry;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Map<String, String> makeTransaction(TransactionDTO transactionDTO) throws Exception {
        Map<String,String> errors = new HashMap<>();

        TrnType trnType = TrnType.fromValue(transactionDTO.getTransactionType());
        if (trnType == null){
            errors.put("transactionType","Invalid transaction type");
        }

        if(trnType == TrnType.TRANSFER && transactionDTO.getAccountTo().isEmpty()){
            errors.put("accountTo","Target account is missing");
        }

        if(transactionDTO.getAmount() <= 0){
            errors.put("amount","Transaction amount cannot less then equal 0");
        }

        if(transactionDTO.getDescription().isEmpty()){
            errors.put("description","Description cannot be empty");
        }

        if(!errors.isEmpty()){
            return errors;
        }

        try {
            Transaction trn = new Transaction();
            Account acc = getAccDetails();
            Account targetAcc = new Account();

            Double amt = transactionDTO.getAmount();
            if (trnType != TrnType.DEPOSIT && amt > acc.getBalance()) {
                throw new Exception("Not enough balance");
            }

            if (acc.getAccountNo().equals(targetAcc.getAccountNo())){
                throw new Exception("Source account cannot same as target account");
            }

            trn.setTranId(generateTransactionId(trnType));
            trn.setAccountFrom(acc.getAccountNo());
            trn.setTransactionType(trnType);
            trn.setUser(userService.me());
            trn.setAmount(amt);
            trn.setDescription(transactionDTO.getDescription());
            trn.setTransactionStatus(TrnStatus.SUCCESS);
            trn.setTransactionDateTime(LocalDateTime.now());

            transactionReposiotry.save(trn);
            accountRepository.save(acc);


            switch (trn.getTransactionType()) {
                case TrnType.DEPOSIT -> {
                    double subtotal = acc.getBalance() + amt;
                    acc.setBalance(subtotal);
                    break;
                }
                case TrnType.WITHDRAWAL -> {
                    double subtotal = acc.getBalance() - amt;
                    acc.setBalance(subtotal);
                    break;
                }
                case TrnType.TRANSFER -> {
                    Optional<Account> findTargetAcc = accountRepository.findByAccountNo(transactionDTO.getAccountTo());
                    if(findTargetAcc.isEmpty() || findTargetAcc.get().getAccountStatus() != AccStatus.ACTIVE){
                        throw new Exception("Target account not exist or not active");
                    }
                    targetAcc = findTargetAcc.get();

                    double subtotal = acc.getBalance() - amt;
                    acc.setBalance(subtotal);
                    double targetSubtotal = targetAcc.getBalance() + amt;
                    targetAcc.setBalance(targetSubtotal);

                    trn.setAccountTo(targetAcc.getAccountNo());
                    accountRepository.save(targetAcc);
                }
            }



        }catch (Exception e){
            throw new Exception(e.getMessage());
        }


        return Collections.singletonMap("msg", "Success");
    }

    private String generateTransactionId(TrnType type) throws Exception {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));



        int seq = globalService.getSequence("TRANSACTION");

        return String.format("TRN%d%s%010d", type.getValue(), datePrefix, seq);
    }

    private Account getAccDetails() throws Exception {
        User user = userService.me();

        Optional<Account> findAcc = accountRepository.findByUsername(user.getUsername());
        if(findAcc.isEmpty()){
            throw new Exception("No account no found");
        }

        Account acc = findAcc.get();

        if(acc.getAccountStatus() != AccStatus.ACTIVE){
            throw new Exception("Your account number is " + AccStatus.ACTIVE.toString()
            + " due to "+ acc.getRemarks()
            + " Please contact support!");
        }

        return acc;

    }
}
