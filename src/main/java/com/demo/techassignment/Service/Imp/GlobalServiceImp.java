package com.demo.techassignment.Service.Imp;

import com.demo.techassignment.DTO.StaffCreationDTO;
import com.demo.techassignment.DTO.UserRegisterDTO;
import com.demo.techassignment.Model.Enum.Role;
import com.demo.techassignment.Model.Enum.UserStatus;
import com.demo.techassignment.Model.Sequence;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.SequenceRepository;
import com.demo.techassignment.Repository.UserRepository;
import com.demo.techassignment.Service.GlobalService;
import com.demo.techassignment.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GlobalServiceImp implements GlobalService {

    @Autowired
    private SequenceRepository sequenceRepository;


    private final UserService userService;
    private final UserRepository userRepository;


    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public GlobalServiceImp(@Lazy UserService userService, @Lazy UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public Integer getSequence(String table) throws Exception {
        try{
            Optional<Sequence> findTable = sequenceRepository.findByTable(table);
            Sequence sequence = new Sequence();
            if(findTable.isPresent()){
                sequence = findTable.get();
                int currSeq = sequence.getCounter() + 1;
                sequence.setCounter(currSeq);
                sequenceRepository.save(sequence);

            }else {
                sequence.setTable(table);
                sequence.setCounter(1);
            }


            sequenceRepository.save(sequence);

            return sequence.getCounter();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String dummyData() throws Exception {
      try{
          UserRegisterDTO userRegisterDTO = new UserRegisterDTO("user1","user1","user1@gmail.com","012-00199921","0111261-10-9727","Test@9999");
          userService.UserRegistartion(userRegisterDTO);
          UserRegisterDTO userRegisterDTO2 = new UserRegisterDTO("user2","user2","user2@gmail.com","012-00199922","0111261-10-9723","Test@9999");
          userService.UserRegistartion(userRegisterDTO2);
          UserRegisterDTO userRegisterDTO3 = new UserRegisterDTO("user3","user3","user3@gmail.com","012-00199923","0111261-10-9721","Test@9999");
          userService.UserRegistartion(userRegisterDTO3);

          User admin = new User();
          admin.setUsername("admin");
          admin.setEmail("admin@gmail.com");
          admin.setContact("012-8457781");
          admin.setName("admin");
          admin.setUserStatus(UserStatus.ACTIVE);
          admin.setRole(Role.ADMIN);
          admin.setIdNo("-");
          admin.setPass(passwordEncoder.encode("Test@9999"));
          userRepository.save(admin);

          return "Success";
      }catch (Exception e){
          throw new Exception(e.getMessage());
      }
    }
}
