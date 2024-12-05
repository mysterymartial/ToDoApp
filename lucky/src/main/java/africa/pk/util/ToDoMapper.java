package africa.pk.util;

import africa.pk.data.model.ToDo;
import africa.pk.dto.request.ToDoRequestDto;

import africa.pk.dto.response.ToDoResponseDto;

public class ToDoMapper {

    public static ToDo toToDo(ToDoRequestDto dtoDetails){
            ToDo toDo = new ToDo();
            toDo.setUserName(dtoDetails.getUserName());
            toDo.setPassword(dtoDetails.getPassword());
            toDo.setEmail(dtoDetails.getEmail());
            return toDo;

    }
    public static ToDoResponseDto toDoResponseDto(ToDo toDo){
            ToDoResponseDto response = new ToDoResponseDto();
            response.setEmail(toDo.getEmail());
            response.setUserName(toDo.getUserName());
            response.setPassword(toDo.getPassword());
            response.setMessage("Successful");
            response.setIsLocked(String.valueOf(toDo.isLocked()));
            return response;
    }
}
