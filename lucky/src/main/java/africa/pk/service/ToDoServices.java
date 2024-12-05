package africa.pk.service;

import africa.pk.data.model.ToDo;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.request.ToDoRequestDto;
import africa.pk.dto.response.ToDoResponseDto;

public interface ToDoServices {
    ToDoResponseDto login(ToDoRequestDto toDoDetails);
    ToDoResponseDto register(ToDoRequestDto toDoDetails);
    ToDoResponseDto logout();
    ToDoResponseDto addActivity(ToDoEntryRequestDto toDoDetails);
    ToDoResponseDto deleteActivity(ToDoEntryRequestDto toDoDetails);
    ToDoResponseDto updateActivity(ToDoEntryRequestDto toDoDetails);
    ToDoResponseDto searchActivity(ToDoEntryRequestDto toDoDetails);
    ToDo findUserByUsername(String userName);

}
