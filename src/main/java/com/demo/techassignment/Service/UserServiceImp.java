package com.demo.techassignment.Service;

import com.demo.techassignment.DTO.UserRegisterDTO;
import com.demo.techassignment.Model.Status;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;

    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
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
            user.setPass(userRegisterDTO.getPass());
            user.setStatus(Status.ACTIVE);

            userRepository.save(user);
            return "Success";
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }
}
