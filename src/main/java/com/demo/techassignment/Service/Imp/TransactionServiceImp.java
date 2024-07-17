package com.demo.techassignment.Service.Imp;

import com.demo.techassignment.DTO.*;
import com.demo.techassignment.Model.Account;
import com.demo.techassignment.Model.Enum.AccStatus;
import com.demo.techassignment.Model.Enum.Role;
import com.demo.techassignment.Model.Enum.TrnStatus;
import com.demo.techassignment.Model.Enum.TrnType;
import com.demo.techassignment.Model.Transaction;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.AccountRepository;
import com.demo.techassignment.Repository.TransactionReposiotry;
import com.demo.techassignment.Service.GlobalService;
import com.demo.techassignment.Service.TransactionService;
import com.demo.techassignment.Service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImp implements TransactionService {

    private final TransactionReposiotry transactionReposiotry;
    private final AccountRepository accountRepository;

    private final UserService userService;

    private final GlobalService globalService;

    public TransactionServiceImp(TransactionReposiotry transactionReposiotry, AccountRepository accountRepository, UserService userService, GlobalService globalService) {
        this.transactionReposiotry = transactionReposiotry;
        this.accountRepository = accountRepository;
        this.userService = userService;
        this.globalService = globalService;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Map<String, Object> makeTransaction(TransactionDTO transactionDTO) throws Exception {
        Map<String,String> errors = new HashMap<>();

        TrnType trnType = TrnType.fromValue(transactionDTO.getTransactionType());
        if (trnType == null){
            errors.put("transactionType","Invalid transaction type");
        }

        if((trnType == TrnType.TRANSFER || trnType == TrnType.DUITNOW) && transactionDTO.getAccountTo().isEmpty()){
            errors.put("accountTo","Target account is missing");
        }

        if(transactionDTO.getAmount() <= 0){
            errors.put("amount","Transaction amount cannot less then equal 0");
        }

        if(transactionDTO.getDescription().isEmpty()){
            errors.put("description","Description cannot be empty");
        }

        if(!errors.isEmpty()){
            return Map.of("errors",errors);
        }

        try {
            Transaction trn = new Transaction();
            Account acc = getAccDetails();
            Account targetAcc = new Account();

            Double amt = transactionDTO.getAmount();
            if (trnType != TrnType.DEPOSIT && amt > acc.getBalance()) {
                throw new Exception("Not enough balance");
            }

            if (acc.getAccountNo().equals(transactionDTO.getAccountTo())){
                throw new Exception("Source account cannot same as target account");
            }

            trn.setTranId(generateTransactionId(trnType));
            trn.setAccountFrom(acc.getAccountNo());
            trn.setTransactionType(trnType);
            trn.setUser(userService.me());
            trn.setAmount(amt);
            trn.setDescription(transactionDTO.getDescription());
            trn.setTransactionStatus(TrnStatus.COMPLETED);
            trn.setTransactionDateTime(LocalDateTime.now());
            trn.setActionBy(userService.me());




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
                case TrnType.DUITNOW -> {
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
                case TrnType.TRANSFER -> {
                    Optional<Account> findTargetAcc = accountRepository.findByAccountNo(transactionDTO.getAccountTo());
                    if(findTargetAcc.isEmpty() || findTargetAcc.get().getAccountStatus() != AccStatus.ACTIVE){
                        throw new Exception("Target account not exist or not active");
                    }
                    targetAcc = findTargetAcc.get();

                    double subtotal = acc.getBalance() - amt;
                    acc.setBalance(subtotal);
                    double targetSubtotal = targetAcc.getTempBalance() + amt;
                    targetAcc.setTempBalance(targetSubtotal);

                    trn.setAccountTo(targetAcc.getAccountNo());
                    trn.setTransactionStatus(TrnStatus.PENDING);
                    accountRepository.save(targetAcc);
                }
            }

            transactionReposiotry.save(trn);
            accountRepository.save(acc);

            Map<String,String> response = new HashMap<>();
            response.put("trnId", trn.getTranId());
            response.put("trnDesc", trn.getDescription());
            response.put("trnDT",trn.getTransactionDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a")));
            response.put("trnStatus", trn.getTransactionStatus().name());
            response.put("trnType", trn.getTransactionType().name());
            if (trn.getTransactionStatus() == TrnStatus.PENDING){
                response.put("info","You can do cancellation for this transaction");
            }

            return Map.of("msg", "Success", "trn", response);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }



    }

    @Override
    public Map<String, Object> manageTransaction(ManageTransactionDTO manageTransactionDTO) {
        Map<String,String> response = new HashMap<>();
        User me = userService.me();

        Optional<Transaction> findTrn = transactionReposiotry.findByTranId(manageTransactionDTO.getTrnId());
        TrnStatus trnStatus = TrnStatus.fromValue(manageTransactionDTO.getTrnStatus());

        if(findTrn.isEmpty()){
            response.put("tarId","Invalid transaction Id");
        }

        if(trnStatus == null){
            response.put("trnStatus","Invalid transaction status");
        }else if (me.getRole() == Role.USER && trnStatus != TrnStatus.CANCELED){
            response.put("trnStatus","You can do cancellation only.");
        } else if (trnStatus != TrnStatus.COMPLETED && trnStatus != TrnStatus.CANCELED){
            response.put("trnStatus","The status should be COMPLETED or CANCELED only.");
        }


        Transaction trn = findTrn.get();

        if (trn.getTransactionStatus() != TrnStatus.PENDING){
            response.put("trnStatus","No action are allow.");
        }

        trn.setTransactionStatus(trnStatus);
        trn.setRemarks(manageTransactionDTO.getRemarks());
        trn.setActionBy(me);

        Optional<Account> findSrcAcc = accountRepository.findByAccountNo(trn.getAccountFrom());
        Optional<Account> findTargetAcc = accountRepository.findByAccountNo(trn.getAccountTo());

        if(findSrcAcc.isEmpty()){
            response.put("accountTo","Target account not found");
        }

        if (findTargetAcc.isEmpty()){
            response.put("accountTo","Target account not found");
        }

        if (!response.isEmpty()){
            return Map.of("errors",response);
        }

        Account srcAcc = findSrcAcc.get();
        Account targetAcc = findTargetAcc.get();

        switch (trnStatus){
            case TrnStatus.COMPLETED -> {
                double tempBalance = targetAcc.getTempBalance();
                double balance  = targetAcc.getBalance();
                double trnAmt   = trn.getAmount();

                tempBalance = tempBalance - trnAmt;
                balance = balance + trnAmt;

                targetAcc.setTempBalance(tempBalance);
                targetAcc.setBalance(balance);
                accountRepository.save(targetAcc);
            }
            case TrnStatus.CANCELED -> {
                double tempBalance = targetAcc.getTempBalance();
                double balance  = srcAcc.getBalance();
                double trnAmt   = trn.getAmount();

                tempBalance = tempBalance - trnAmt;
                balance = balance + trnAmt;

                targetAcc.setTempBalance(tempBalance);
                srcAcc.setBalance(balance);

                accountRepository.save(targetAcc);
                accountRepository.save(srcAcc);
            }
        }
        transactionReposiotry.save(trn);

        return Map.of("msg","success");
    }

    @Override
    public Map<String, Object> getTransactionHistory(TrnHistoryDTO trnHistoryDTO) throws Exception {
        try{
            Map<String, String> errors = new HashMap<>();
            User user = userService.me();
            Integer page = trnHistoryDTO.getPage() != null ? trnHistoryDTO.getPage() : 0;

            Sort.Direction direction = "A".equals(trnHistoryDTO.getSort()) ? Sort.Direction.ASC : Sort.Direction.DESC;
            PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(direction, "transactionDateTime"));

            TrnStatus trnStatus = null;
            if (trnHistoryDTO.getTrnStatus() != null && trnHistoryDTO.getTrnStatus() != 0) {
                try {
                    trnStatus = TrnStatus.fromValue(trnHistoryDTO.getTrnStatus());
                } catch (IllegalArgumentException e) {
                    errors.put("trnStatus", "Invalid transaction status");
                }
            }

            TrnType trnType = null;
            if (trnHistoryDTO.getTrnType() != null && trnHistoryDTO.getTrnType() != 0) {
                try {
                    trnType = TrnType.fromValue(trnHistoryDTO.getTrnType());
                } catch (IllegalArgumentException e) {
                    errors.put("trnType", "Invalid transaction type");
                }
            }

            if (trnHistoryDTO.getTrnId() != null && trnHistoryDTO.getTrnId().isEmpty()) {
                trnHistoryDTO.setTrnId(null);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDateTime dateFrom = null;
            LocalDateTime dateTo = null;
            if (trnHistoryDTO.getDateFrom() != null && !trnHistoryDTO.getDateFrom().isEmpty()) {
                try {
                    dateFrom = LocalDate.parse(trnHistoryDTO.getDateFrom(), formatter).atStartOfDay();
                } catch (DateTimeParseException e) {
                    errors.put("dateFrom","Invalid dateFrom format. Expected format: yyyy-MM-dd");
                }
            }

            if (trnHistoryDTO.getDateTo() != null && !trnHistoryDTO.getDateTo().isEmpty()) {
                try {
                    dateTo = LocalDate.parse(trnHistoryDTO.getDateTo(), formatter).atTime(23,59,59);
                } catch (DateTimeParseException e) {
                    errors.put("dateTo","Invalid dateTo format. Expected format: yyyy-MM-dd");
                }
            }



            if (!errors.isEmpty()) {
                return Map.of("errors", errors);
            }


            Integer userId = user.getId();
            if (user.getRole() != Role.USER){
                if (trnHistoryDTO.getUserId() != 0){
                    userId = trnHistoryDTO.getUserId();
                }else {
                    userId = null;
                }
            }
            String trnId = trnHistoryDTO.getTrnId();

            Page<Transaction> findTrn = transactionReposiotry.findTransactionByFilters(
                    userId,
                    trnId,
                    trnStatus,
                    trnType,
                    dateFrom,
                    dateTo,
                    pageRequest);

            List<RtnTrnHisDTO> trn = findTrn.getContent().stream().map(t -> {
                RtnTrnHisDTO rtnTrnHisDTO = new RtnTrnHisDTO();
                rtnTrnHisDTO.setUsername(t.getUser().getUsername());
                rtnTrnHisDTO.setTrnId(t.getTranId());
                rtnTrnHisDTO.setTransactionDateTime(t.getTransactionDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a")));
                rtnTrnHisDTO.setAccountFrom(t.getAccountFrom());
                rtnTrnHisDTO.setAccountTo(t.getAccountTo());
                rtnTrnHisDTO.setAmount(t.getAmount());
                rtnTrnHisDTO.setDescription(t.getDescription());
                rtnTrnHisDTO.setRemarks(t.getRemarks());
                rtnTrnHisDTO.setTrnStatus(t.getTransactionStatus().name());
                rtnTrnHisDTO.setTrnType(t.getTransactionType().name());
                return rtnTrnHisDTO;
            }).collect(Collectors.toList());

            Page<RtnTrnHisDTO> response = new PageImpl<>(trn,
                    PageRequest.of(
                            pageRequest.getPageNumber(),
                            pageRequest.getPageSize(),
                            pageRequest.getSort()
                    ), findTrn.getTotalElements()
            );

            return Map.of("data", response);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> manageAcc(ManageAccountDTO manageAccountDTO) throws Exception {
        try{
            Map<String,String> errors = new HashMap<>();

            AccStatus accStatus = AccStatus.fromValue(manageAccountDTO.getAccStatus());
            if (accStatus == null){
                errors.put("accStatus","Invalid Status");
            }

            Optional<Account> findAcc = accountRepository.findByAccountNo(manageAccountDTO.getAccNo());
            if(findAcc.isEmpty()){
                errors.put("accNo","Invalid account no");
            }

            if(!errors.isEmpty()){
                return Map.of("errors",errors);
            }

            Account acc = findAcc.get();
            acc.setAccountStatus(accStatus);
            String remarks = "";
            if (acc.getRemarks() == null){
                remarks = manageAccountDTO.getRemarks();
            }else{
                remarks = acc.getRemarks() + "|" + manageAccountDTO.getRemarks();
            }
            acc.setRemarks(remarks);

            accountRepository.save(acc);

            return Map.of("msg","Account Updated");
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
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
            throw new Exception("Your account number is " + acc.getAccountStatus()
            + " due to "+ acc.getRemarks()
            + " Please contact support!");
        }

        return acc;

    }
}
