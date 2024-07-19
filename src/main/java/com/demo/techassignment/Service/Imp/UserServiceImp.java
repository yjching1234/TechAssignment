package com.demo.techassignment.Service.Imp;

import com.demo.techassignment.Configure.JwtTokenUtil;
import com.demo.techassignment.DTO.*;
import com.demo.techassignment.Model.Account;
import com.demo.techassignment.Model.Enum.AccStatus;
import com.demo.techassignment.Model.Enum.Role;
import com.demo.techassignment.Model.Enum.UserStatus;
import com.demo.techassignment.Model.Token;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.AccountRepository;
import com.demo.techassignment.Repository.TokenRepository;
import com.demo.techassignment.Repository.UserRepository;
import java.text.DecimalFormat;

import com.demo.techassignment.Service.GlobalService;
import com.demo.techassignment.Service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;


    private final GlobalService globalService;


    private final AuthenticationManager authenticationManager;


    private final JwtTokenUtil jwtTokenUtil;


    private final MyUserDetailService userDetailsService;


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserServiceImp(UserRepository userRepository, AccountRepository accountRepository, TokenRepository tokenRepository, GlobalService globalService, AuthenticationManager authenticationManager1, JwtTokenUtil jwtTokenUtil, MyUserDetailService userDetailsService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
        this.globalService = globalService;
        this.authenticationManager = authenticationManager1;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Map<String, Object> UserRegistartion(UserRegisterDTO userRegisterDTO) throws Exception {
        try{
            Map<String,String> errors = new HashMap<>();
            Optional<User> existingContact = userRepository.findByContact(userRegisterDTO.getContact());
            if (existingContact.isPresent()) {
                errors.put("contact","This contact number is already in use.");
            }

            Optional<User> existingEmail = userRepository.findByEmail(userRegisterDTO.getEmail());
            if (existingEmail.isPresent()) {
                errors.put("email","This email address is already registered.");
            }

            Optional<User> existingUsername = userRepository.findByUsername(userRegisterDTO.getUsername());
            if (existingUsername.isPresent()) {
                errors.put("username","This username is already taken.");
            }

            if (!errors.isEmpty()) {
                return Map.of("errors",errors);
            }

            User user = new User();
            user.setName(userRegisterDTO.getName());
            user.setEmail(userRegisterDTO.getEmail());
            user.setUsername(userRegisterDTO.getUsername());
            user.setContact(userRegisterDTO.getContact());
            user.setPass(passwordEncoder.encode(userRegisterDTO.getPass()));
            user.setRole(Role.USER);
            user.setUserStatus(UserStatus.ACTIVE);



            userRepository.save(user);

            CreateAccount(user);

            return Map.of("msg","Register successfully");
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public Map<String, String> UserLogin(UserLoginDTO userLoginDTO) throws Exception {
       try {

           Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPass())
           );

           SecurityContextHolder.getContext().setAuthentication(authentication);

           UserDetails userDetails = userDetailsService.loadUserByUsername(userLoginDTO.getUsername());
           String token = jwtTokenUtil.generateToken(userDetails);

           User user = userRepository.findByUsername(userLoginDTO.getUsername()).orElseThrow();

           revokeAllTokenByUser(user);
           saveUserToken(token,user);

           Map<String,String> response = new HashMap<>();
           response.put("token",token);
           response.put("username",userLoginDTO.getUsername());

           return response;
       }catch (BadCredentialsException e){
           throw new Exception("Invalid username or password", e);
       } catch (Exception e){
           throw new Exception(e.getMessage());
       }
    }

    @Override
    public Map<String, String> Logout() throws Exception {
        try{
            revokeAllTokenByUser(me());
            return Map.of("msg","Logout successfully");
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public User me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                String username = ((UserDetails) principal).getUsername();
                // Fetch the user details from the database using the username
                return userRepository.findByUsername(username).orElseThrow();
            } else if (principal instanceof String) {
                String username = principal.toString();
                // Fetch the user details from the database using the username
                return userRepository.findByUsername(username).orElseThrow();
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getUserProfile() {
        User user = me();



        DecimalFormat df = new DecimalFormat("#,##0.00");

        Map<String,String> profile = new HashMap<>();
        profile.put("name",user.getName());
        profile.put("username",user.getUsername());
        profile.put("email",user.getEmail());
        profile.put("Contact",user.getContact());
        profile.put("Role", user.getRole().toString());

        if (user.getRole() == Role.USER){
            Account acc = accountRepository.findByUsername(user.getUsername()).orElseThrow();
            profile.put("accountNo",acc.getAccountNo());
            profile.put("totalBalance", df.format(acc.getBalance() + acc.getTempBalance()));
            profile.put("availableBalance", df.format(acc.getBalance()));
            profile.put("tempBalance", df.format(acc.getTempBalance()));
            profile.put("accStatus",acc.getAccountStatus().toString());
        }


        return profile;
    }

    @Override
    public Map<String, Object> staffCreation(StaffCreationDTO staffCreationDTO) throws Exception {
        try{
            Map<String,String> errors = new HashMap<>();
            Optional<User> existingContact = userRepository.findByContact(staffCreationDTO.getContact());
            if (existingContact.isPresent()) {
                errors.put("contact","This contact number is already in use.");
            }

            Optional<User> existingEmail = userRepository.findByEmail(staffCreationDTO.getEmail());
            if (existingEmail.isPresent()) {
                errors.put("email","This email address is already registered.");
            }



            Role role = Role.fromValue(staffCreationDTO.getRole());
            String prefix = "";

            switch (role){
                case Role.STAFF -> prefix = "ST";
                case Role.ADMIN -> prefix = "AD";
                case Role.USER -> errors.put("role","You cannot create User account");
                case null -> errors.put("role","Invalid Role");
            }


            if(!errors.isEmpty()){
                return Map.of("errors", errors);
            }

            String staffId = String.format("%s%05d",prefix, globalService.getSequence("EMPLOYEE"));

            User user = new User();
            user.setName(staffCreationDTO.getName());
            user.setEmail(staffCreationDTO.getEmail());
            user.setUsername(staffId);
            user.setContact(staffCreationDTO.getContact());
            user.setPass(passwordEncoder.encode(staffCreationDTO.getPass()));
            user.setRole(role);
            user.setUserStatus(UserStatus.ACTIVE);

            userRepository.save(user);

            return Map.of("msg", Map.of("staffId",staffId));

        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String createDummyData() throws Exception {
        return globalService.dummyData();
    }

    @Override
    public Map<String, Object> editUser(EditUserDTO editUserDTO) throws Exception {
        try{
            Map<String,String> errors = new HashMap<>();
            User user = new User();
            User currUser = me();
            Role role = null;
            UserStatus userStatus = null;

            if(editUserDTO.getUsername().equals("admin")){
                errors.put("role","Super admin not allow to edit");
            }

            if(currUser.getRole() == Role.USER){
                user = currUser;
            }else{
                if(editUserDTO.getUsername() == null || editUserDTO.getUsername().isEmpty()){
                    errors.put("username","Username is missing");
                }else {
                    Optional<User> findUser = userRepository.findByUsername(editUserDTO.getUsername());
                    if(findUser.isEmpty()){
                        errors.put("username","Invalid username");
                    }else if (findUser.get().getRole() != Role.USER && me().getRole() != Role.ADMIN) {
                        errors.put("role","You cannot edit this user.");
                    }else{
                        user = findUser.get();
                    }
                }

            }

            if(currUser.getRole() != Role.USER){
                if (editUserDTO.getAccStatus() != null && editUserDTO.getAccStatus() != 0){
                    userStatus = UserStatus.fromValue(editUserDTO.getAccStatus());
                    if (userStatus == null){
                        errors.put("accStatus","Invalid status");
                    }
                }
            }

            if(currUser.getRole() == Role.ADMIN){
                if (editUserDTO.getRole() != null && editUserDTO.getRole() != 0){
                    role = Role.fromValue(editUserDTO.getRole());
                    if(role == null){
                        errors.put("role", "Invalid Role");
                    }else if (user.getRole() == Role.USER){
                        errors.put("role", "You cannot edit role for this user");
                    }
                }
            }

            if (!editUserDTO.getPass().isEmpty()){
                if (editUserDTO.getPass().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")){
                    errors.put("pass","Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character (e.g., @, #, $, %, &, etc.)");
                }
            }

            if(!errors.isEmpty()){
                return Map.of("errors",errors);
            }


            Optional<User> existingContact = userRepository.findByContact(editUserDTO.getContact());
            if (existingContact.isPresent() && !existingContact.get().getUsername().equals(user.getUsername())) {
                errors.put("contact","This contact number is already in use.");
            }

            Optional<User> existingEmail = userRepository.findByEmail(editUserDTO.getEmail());
            if (existingEmail.isPresent() && !existingEmail.get().getUsername().equals(user.getUsername())) {
                errors.put("email","This email address is already registered.");
            }

            if(!errors.isEmpty()){
                return Map.of("errors",errors);
            }

            user.setName(editUserDTO.getName());
            user.setContact(editUserDTO.getContact());
            user.setEmail(editUserDTO.getEmail());

            if (!editUserDTO.getPass().isEmpty()){
                user.setPass(passwordEncoder.encode(editUserDTO.getPass()));
            }

            if (role != null){
                user.setRole(role);
            }
            if (userStatus != null){
                user.setUserStatus(userStatus);
            }

            userRepository.save(user);

            return Map.of("msg","Update successfully");
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getUserDetailByUsername(GetUserDto getUserDto) throws Exception {
        try{
            Optional<User> findUser = userRepository.findByUsername(getUserDto.getUsername());
            if(findUser.isEmpty()){
                return Map.of("errors","Invalid Username");
            }

            User user = findUser.get();


            DecimalFormat df = new DecimalFormat("#,##0.00");

            Map<String,String> profile = new HashMap<>();
            profile.put("name",user.getName());
            profile.put("username",user.getUsername());
            profile.put("email",user.getEmail());
            profile.put("Contact",user.getContact());
            profile.put("Role", user.getRole().toString());

            if (user.getRole() == Role.USER){
                Account acc = accountRepository.findByUsername(user.getUsername()).orElseThrow();
                profile.put("accountNo",acc.getAccountNo());
                profile.put("totalBalance", df.format(acc.getBalance() + acc.getTempBalance()));
                profile.put("availableBalance", df.format(acc.getBalance()));
                profile.put("tempBalance", df.format(acc.getTempBalance()));
                profile.put("accStatus",acc.getAccountStatus().toString());
            }


            return Map.of("data",profile);


        }catch (Exception e){
            throw  new Exception(e.getMessage());
        }
    }

    private void CreateAccount(User user){
        Account acc = new Account();
        String newAccNo;

        do {
            newAccNo = acc.generateAccountNo();
        } while (accountRepository.findByAccountNo(newAccNo).isPresent());

        acc.setUsername(user.getUsername());
        acc.setAccountNo(newAccNo);
        acc.setBalance(0.0);
        acc.setTempBalance(0.0);
        acc.setAccountStatus(AccStatus.ACTIVE);

        accountRepository.save(acc);
    }

    private void saveUserToken(String jwt, User user) {
        Token token = new Token();
        token.setToken(jwt);
        token.setLoggout(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    private void revokeAllTokenByUser(User user) {
        List<Token> validateTokenByUser = tokenRepository.findAllTokenByUsername(user.getUsername());
        if(!validateTokenByUser.isEmpty()){
            validateTokenByUser.forEach(t-> t.setLoggout(true));
        }

        tokenRepository.saveAll(validateTokenByUser);
    }
}
