package com.demo.techassignment.Controller;

import com.demo.techassignment.DTO.UserRegisterDTO;
import com.demo.techassignment.Service.UserService;
import com.demo.techassignment.Service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Test ok");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(UserRegisterDTO userRegisterDTO){
        try {
            String response = userService.UserRegistartion(userRegisterDTO);
            return ResponseEntity.ok(response);
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
