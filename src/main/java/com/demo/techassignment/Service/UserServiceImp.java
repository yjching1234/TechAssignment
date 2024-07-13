package com.demo.techassignment.Service;

import com.demo.techassignment.Configure.JwtTokenUtil;
import com.demo.techassignment.DTO.UserRegisterDTO;
import com.demo.techassignment.Model.Account;
import com.demo.techassignment.Model.Enum.AccStatus;
import com.demo.techassignment.Model.Enum.UserStatus;
import com.demo.techassignment.Model.Token;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.AccountRepository;
import com.demo.techassignment.Repository.TokenRepository;
import com.demo.techassignment.Repository.UserRepository;
import java.text.DecimalFormat;
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

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;


    private final AuthenticationManager authenticationManager;


    private final JwtTokenUtil jwtTokenUtil;


    private final MyUserDetailService userDetailsService;


    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public UserServiceImp(UserRepository userRepository, AccountRepository accountRepository, AuthenticationManager authenticationManager, TokenRepository tokenRepository, AuthenticationManager authenticationManager1, JwtTokenUtil jwtTokenUtil, MyUserDetailService userDetailsService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager1;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public String UserRegistartion(UserRegisterDTO userRegisterDTO) throws Exception {
        try{
            List<String> errors = new ArrayList<>();
            Optional<User> existingContact = userRepository.findByContact(userRegisterDTO.getContact());
            if (existingContact.isPresent()) {
                errors.add("This contact number is already in use.");
            }

            Optional<User> existingEmail = userRepository.findByEmail(userRegisterDTO.getEmail());
            if (existingEmail.isPresent()) {
                errors.add("This email address is already registered.");
            }

            Optional<User> existingUsername = userRepository.findByUsername(userRegisterDTO.getUsername());
            if (existingUsername.isPresent()) {
                errors.add("This username is already taken.");
            }

            if (!errors.isEmpty()) {
                // Build error message
                StringBuilder errorMessage = new StringBuilder("Registration failed. \n");
                for (String error : errors) {
                    errorMessage.append(error).append("\n");
                }
                throw new Exception(errorMessage.toString().trim());
            }

            User user = new User();
            user.setName(userRegisterDTO.getName());
            user.setEmail(userRegisterDTO.getEmail());
            user.setUsername(userRegisterDTO.getUsername());
            user.setContact(userRegisterDTO.getContact());
            user.setPass(passwordEncoder.encode(userRegisterDTO.getPass()));
            user.setUserStatus(UserStatus.ACTIVE);



            userRepository.save(user);

            CreateAccount(user);

            return "Success";
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public Map<String, String> UserLogin(UserRegisterDTO userRegisterDTO) throws Exception {
       try {

           Authentication authentication = authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(userRegisterDTO.getUsername(), userRegisterDTO.getPass())
           );

           SecurityContextHolder.getContext().setAuthentication(authentication);

           UserDetails userDetails = userDetailsService.loadUserByUsername(userRegisterDTO.getUsername());
           String token = jwtTokenUtil.generateToken(userDetails);

           User user = userRepository.findByUsername(userRegisterDTO.getUsername()).orElseThrow();

           revokeAllTokenByUser(user);
           saveUserToken(token,user);

           Map<String,String> response = new HashMap<>();
           response.put("token",token);
           response.put("username",userRegisterDTO.getUsername());

           return response;
       }catch (BadCredentialsException e){
           throw new Exception("Invalid username or password", e);
       } catch (Exception e){
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
        Account acc = accountRepository.findByUsername(user.getUsername()).orElseThrow();

        DecimalFormat df = new DecimalFormat("#,##0.00");

        Map<String,String> profile = new HashMap<>();
        profile.put("name",user.getName());
        profile.put("username",user.getUsername());
        profile.put("email",user.getEmail());
        profile.put("Contact",user.getContact());
        profile.put("UpdateAt",user.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        profile.put("CreatedAt",user.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss")));
        profile.put("accountNo",acc.getAccountNo());
        profile.put("balance", df.format(acc.getBalance()));
        profile.put("accStatus",acc.getAccountStatus().toString());

        return profile;
    }

    private void CreateAccount(User user){
        Account acc = new Account();
        String newAccNo = "";

        while (true){
            newAccNo = acc.generateAccountNo();
            if (accountRepository.findByAccountNo(newAccNo).isPresent()){
                continue;
            }else {
                break;
            }
        }

        acc.setUsername(user.getUsername());
        acc.setAccountNo(newAccNo);
        acc.setBalance(0.0);
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
            validateTokenByUser.forEach(t->{
                t.setLoggout(true);
            });
        }

        tokenRepository.saveAll(validateTokenByUser);
    }
}
