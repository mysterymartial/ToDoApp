package africa.pk.controller;

import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.exception.InvalidInput;
import africa.pk.service.ToDoEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/entry/services")
public class ToDoEntryController {
    @Autowired
    private ToDoEntryService toDoEntryService;
    @PostMapping
    public ResponseEntity<ToDoEntryResponseDto> createToDoEntry(@RequestBody ToDoEntryRequestDto data){
        try {
            ToDoEntryResponseDto dto = toDoEntryService.createToDoList(data);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (InvalidInput e) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ToDoEntryResponseDto> deleteToDoEntry(@RequestBody ToDoEntryRequestDto data){
        try {
            ToDoEntryResponseDto responseDto = toDoEntryService.deleteToDoList(data);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);

        } catch (InvalidInput e) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

    }
//    @PutMapping("/{id}")
//    public ResponseEntity<ToDoEntryResponseDto> updateToDoEntry(@RequestBody ToDoEntryRequestDto data){
//        try {
//            ToDoEntryResponseDto responseDto = toDoEntryService.updateToDoList(data);
//            return new ResponseEntity<>(responseDto, HttpStatus.OK);
//        } catch (InvalidInput e) {
//            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
//        }
//    }
    @GetMapping("/{id}")
    public ResponseEntity<ToDoEntryResponseDto> searchToDoEntry(@RequestBody ToDoEntryRequestDto data){
        try {
            ToDoEntryResponseDto responseDto = toDoEntryService.searchToDoList(data);
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (InvalidInput e) {
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }

    }


}
