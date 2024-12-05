package africa.pk.service;

import africa.pk.data.model.ToDoEntry;
import africa.pk.data.repository.ToDoEntryRepository;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.exception.InvalidInput;
import africa.pk.util.ToDoEntryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ToDoEntryServiceImpl implements ToDoEntryService{
    @Autowired
    private ToDoEntryRepository toDoEntryRepository;
    @Override
    public ToDoEntryResponseDto createToDoList(ToDoEntryRequestDto entryRequestDto) {
        if(entryRequestDto.getTitle() == null || entryRequestDto.getTitle().isBlank()) {
            throw new InvalidInput("Title cannot be null or blank");
        }
        if(entryRequestDto.getDescription() == null || entryRequestDto.getDescription().isBlank()) {
            throw new InvalidInput("Description cannot be null or blank");
        }
        ToDoEntry entry = ToDoEntryMapper.toDoEntry(entryRequestDto);
        ToDoEntry savedEntry = toDoEntryRepository.save(entry);
        return ToDoEntryMapper.toDoEntryResponseDto(savedEntry);
    }

    @Override
    public ToDoEntryResponseDto deleteToDoList(ToDoEntryRequestDto entryRequestDto) {
        if(entryRequestDto.equals(null)){
            throw new InvalidInput("Invalid ID provided");

        }

        ToDoEntry entry = toDoEntryRepository.findByid(entryRequestDto.getId());
        if(entry == null){
            throw new InvalidInput("Invalid ID provided");
        }
        toDoEntryRepository.delete(entry);
        return ToDoEntryMapper.toDoEntryResponseDto(entry);





    }

//    @Override
//    public ToDoEntryResponseDto updateToDoList(ToDoEntryRequestDto entryRequestDto) {
//        if(entryRequestDto.equals(null)){
//            throw new InvalidInput("Invalid ID provided");
//        }
//        ToDoEntry entry = toDoEntryRepository.findByid(entryRequestDto.getId());
//        if(entry == null){
//            throw new InvalidInput("Invalid ID provided");
//        }
//        entry.setTitle(entryRequestDto.getTitle());
//        entry.setDescription(entryRequestDto.getDescription());
//        entry.setDueDate(entryRequestDto.getDueDate());
//        entry.setStatus(entryRequestDto.getStatus());
//
//        ToDoEntry updatedEntry = toDoEntryRepository.save(entry);
//
//        return ToDoEntryMapper.toDoEntryResponseDto(entry);
//    }

    @Override
    public ToDoEntryResponseDto searchToDoList(ToDoEntryRequestDto entryRequestDto) {
        if(entryRequestDto == null){
            throw new InvalidInput("Invalid request");
        }
        if(entryRequestDto.getTitle() == null || entryRequestDto.getTitle().isBlank()){
            throw new InvalidInput("Title cannot be null or blank");
        }
        if(entryRequestDto.equals("0")){
            throw new InvalidInput("Invalid ID provided");
        }
        ToDoEntry entry = toDoEntryRepository.findByTitle(entryRequestDto.getTitle());
        if(entry == null){
            throw new InvalidInput("Invalid Title provided");
        }
        return ToDoEntryMapper.toDoEntryResponseDto(entry);
    }


    @Override
    public Object getToDoEntryById(String id) {

        return toDoEntryRepository.findById(id);
    }
    @Override
    public ToDoEntry getToDoEntryByid(String id){
        return toDoEntryRepository.findByid(id);
    }

    @Override
    public ToDoEntry getToDoEntryByTitle(String title) {
        return toDoEntryRepository.findByTitle(title);
    }

}
