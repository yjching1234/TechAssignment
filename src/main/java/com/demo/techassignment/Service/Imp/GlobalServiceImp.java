package com.demo.techassignment.Service.Imp;

import com.demo.techassignment.DTO.StaffCreationDTO;
import com.demo.techassignment.DTO.UserRegisterDTO;
import com.demo.techassignment.Model.Sequence;
import com.demo.techassignment.Model.User;
import com.demo.techassignment.Repository.SequenceRepository;
import com.demo.techassignment.Service.GlobalService;
import com.demo.techassignment.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GlobalServiceImp implements GlobalService {

    @Autowired
    private SequenceRepository sequenceRepository;


    private final UserService userService;

    public GlobalServiceImp(@Lazy UserService userService) {
        this.userService = userService;
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
          UserRegisterDTO userRegisterDTO = new UserRegisterDTO("user1","user1","uer1@gmail.com","012-00199921","Test@9999");
          userService.UserRegistartion(userRegisterDTO);
          UserRegisterDTO userRegisterDTO2 = new UserRegisterDTO("user2","user2","uer2@gmail.com","012-00199922","Test@9999");
          userService.UserRegistartion(userRegisterDTO2);
          UserRegisterDTO userRegisterDTO3 = new UserRegisterDTO("user3","user3","uer3@gmail.com","012-00199923","Test@9999");
          userService.UserRegistartion(userRegisterDTO3);
          UserRegisterDTO userRegisterDTO4 = new UserRegisterDTO("admin1","admin1","ADMIN@gmail.com","012-9928883","Test@9999");
          userService.UserRegistartion(userRegisterDTO4);
          return "Success";
      }catch (Exception e){
          throw new Exception(e.getMessage());
      }
    }
}
