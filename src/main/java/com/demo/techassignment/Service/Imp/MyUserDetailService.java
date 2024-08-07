package com.demo.techassignment.Service.Imp;

import com.demo.techassignment.Model.Enum.UserStatus;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Configuration
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> findUser = userRepository.findByUsername(username);
        if(findUser.isPresent()){
            User user = findUser.get();
            if (user.getUserStatus() == UserStatus.INACTIVE){
                throw new UsernameNotFoundException("USER not active");
            }
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username(username)
                    .password(user.getPass())
                    .roles(user.getRole().toString())
                    .build();

            return userDetails;
        }else {
            throw new UsernameNotFoundException("User not found!!");
        }
    }
}
