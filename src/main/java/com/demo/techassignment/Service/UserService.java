package com.demo.techassignment.Service;

import com.demo.techassignment.DTO.*;
import com.demo.techassignment.Model.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> UserRegistartion(UserRegisterDTO userRegisterDTO) throws Exception;
    Map<String, String> UserLogin(UserLoginDTO userLoginDTO) throws Exception;
    Map<String,String> Logout() throws Exception;
    User me();
    Map<String,String> getUserProfile();

    Map<String, Object> staffCreation(StaffCreationDTO staffCreationDTO) throws Exception;

    String createDummyData() throws Exception;

    Map<String, Object> editUser(EditUserDTO editUserDTO) throws Exception;

    Map<String,Object> getUserDetailByUsername(GetUserDto getUserDto) throws Exception;

}
