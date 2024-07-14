package com.demo.techassignment.Service.Imp;

import com.demo.techassignment.Model.Sequence;
import com.demo.techassignment.Repository.SequenceRepository;
import com.demo.techassignment.Service.GlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GlobalServiceImp implements GlobalService {

    @Autowired
    private SequenceRepository sequenceRepository;
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
}
