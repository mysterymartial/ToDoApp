package africa.pk.controller;

import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.request.ToDoRequestDto;
import africa.pk.dto.response.ToDoResponseDto;
import africa.pk.exception.AuthorizationExpection;
import africa.pk.exception.DuplicateExpection;
import africa.pk.exception.InvalidInput;
import africa.pk.service.ToDoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/todo")
public class ToDoController {
@Autowired
    private ToDoServices toDoServices;
@PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ToDoRequestDto dtoDetails) {
    try {
        ToDoResponseDto responseDto = toDoServices.register(dtoDetails);
        return ResponseEntity.ok(responseDto);

    } catch (InvalidInput e) {
        return ResponseEntity.badRequest().body(e.getMessage());

    } catch (DuplicateExpection e) {
        return ResponseEntity.status(400).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An Unexpected error occured");
    }

}
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody ToDoRequestDto dtoDetails){
    try {
        ToDoResponseDto responseDto = toDoServices.login(dtoDetails);
        return ResponseEntity.ok(responseDto);
    } catch (InvalidInput e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (AuthorizationExpection e) {
        return ResponseEntity.status(401).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An Unexpected error occured");
    }

}
public ResponseEntity<?> logout(){
    try {
        ToDoResponseDto responseDto = toDoServices.logout();
        return ResponseEntity.ok(responseDto);
    } catch (AuthorizationExpection e) {
        return ResponseEntity.status(401).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An Unexpected error occured");
    }
}
@PostMapping("/add/activity")
    public ResponseEntity<?> addActivity(@RequestBody ToDoEntryRequestDto toDoDetalies){
    try {
        ToDoResponseDto responseDto = toDoServices.addActivity(toDoDetalies);
        return ResponseEntity.ok(responseDto);
    } catch (InvalidInput e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (AuthorizationExpection e) {
        return ResponseEntity.status(403).body(e.getMessage());
    }catch (Exception e) {
        return ResponseEntity.internalServerError().body("An Unexpected error occured");
    }

    }
    @DeleteMapping("/delete")
    private ResponseEntity<?> deleteActivity(@RequestBody ToDoEntryRequestDto data){
    try {
        ToDoResponseDto responseDto = toDoServices.deleteActivity(data);
        return ResponseEntity.ok(responseDto);
    } catch (InvalidInput e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (AuthorizationExpection e) {
        return ResponseEntity.status(403).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An Unexpected error occured");
    }
    }
//    @PutMapping("/update")
//    public ResponseEntity<?> updateActivity(@RequestBody ToDoEntryRequestDto data){
//    try {
//        ToDoResponseDto responseDto = toDoServices.updateActivity(data);
//        return ResponseEntity.ok(responseDto);
//    } catch (InvalidInput e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    } catch (AuthorizationExpection e) {
//        return ResponseEntity.status(403).body(e.getMessage());
//    } catch (Exception e) {
//        return ResponseEntity.internalServerError().body("An Unexpected error occured");
//    }
//    }
    @GetMapping("/search")
    public ResponseEntity<?> searchActivity(@RequestBody ToDoEntryRequestDto query){
    try {
        ToDoResponseDto responseDto = toDoServices.searchActivity(query);
        return ResponseEntity.ok(responseDto);
    } catch (InvalidInput e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (AuthorizationExpection e) {
        return ResponseEntity.status(403).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("An Unexpected error occured");
    }
    }
}



