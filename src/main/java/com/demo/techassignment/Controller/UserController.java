package com.demo.techassignment.Controller;

import com.demo.techassignment.DTO.*;
import com.demo.techassignment.Service.GlobalService;
import com.demo.techassignment.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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


    private final UserService userService;


    public UserController(@Lazy UserService userService) {
        this.userService = userService;
    }


//    @GetMapping("/test")
//    public ResponseEntity<String> test(){
//        return ResponseEntity.ok("Test ok");
//    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO){
        try {
            Map<String,Object> response = userService.UserRegistartion(userRegisterDTO);
            if(response.containsKey("errors")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
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

    @GetMapping("/demo")
    ResponseEntity<Object> dummy(){
        try{
            return ResponseEntity.ok(Map.of("msg",userService.createDummyData()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errors",e.getMessage()));
        }
    }

    @PostMapping("/createStaff")
    ResponseEntity<Object> staffCreation(@RequestBody @Valid StaffCreationDTO staffCreationDTO){
        try {
            return ResponseEntity.ok(userService.staffCreation(staffCreationDTO));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errors",e.getMessage()));
        }
    }

    @GetMapping("/logout")
    ResponseEntity<Object> logout(){
        try {
            return ResponseEntity.ok(userService.Logout());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errors",e.getMessage()));
        }
    }

    @PostMapping("/editUser")
    ResponseEntity<Object> userUpdate(@RequestBody @Valid EditUserDTO editUserDTO){
        try {
            Map<String, Object> response = userService.editUser(editUserDTO);
            if(response.containsKey("errors")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errors",e.getMessage()));
        }
    }

    @PostMapping("/getUserInfo")
    ResponseEntity<Object> getUserInfo(@RequestBody @Valid GetUserDto getUserDto){
        try {
            Map<String, Object> response = userService.getUserDetailByUsername(getUserDto);
            if(response.containsKey("errors")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("errors",e.getMessage()));
        }
    }


}
