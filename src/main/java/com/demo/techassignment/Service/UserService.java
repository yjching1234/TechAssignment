package com.demo.techassignment.Service;

import com.demo.techassignment.DTO.UserRegisterDTO;
import com.demo.techassignment.Model.User;

import java.util.Map;

public interface UserService {
    String UserRegistartion(UserRegisterDTO userRegisterDTO) throws Exception;
    Map<String, String> UserLogin(UserRegisterDTO userRegisterDTO) throws Exception;
    User me();
    Map<String,String> getUserProfile();

}
