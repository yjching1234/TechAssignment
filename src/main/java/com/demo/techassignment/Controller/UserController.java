package com.demo.techassignment.Controller;

import com.demo.techassignment.DTO.UserLoginDTO;
import com.demo.techassignment.DTO.UserRegisterDTO;
import com.demo.techassignment.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;



//    @GetMapping("/test")
//    public ResponseEntity<String> test(){
//        return ResponseEntity.ok("Test ok");
//    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO userRegisterDTO){
        try {
            String response = userService.UserRegistartion(userRegisterDTO);
            return ResponseEntity.ok(response);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid UserLoginDTO userLoginDTO){
        try {

            return new ResponseEntity<>(userService.UserLogin(userLoginDTO), HttpStatus.ACCEPTED);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    ResponseEntity<Object> getUser(){
        try{
            return new ResponseEntity<>(userService.getUserProfile(),HttpStatus.ACCEPTED);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



}
