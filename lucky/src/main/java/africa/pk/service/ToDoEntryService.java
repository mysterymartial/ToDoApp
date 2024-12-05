package africa.pk.service;

import africa.pk.data.model.ToDoEntry;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.dto.response.ToDoResponseDto;

public interface ToDoEntryService {
    ToDoEntryResponseDto createToDoList(ToDoEntryRequestDto entryRequestDto);
    ToDoEntryResponseDto deleteToDoList(ToDoEntryRequestDto entryRequestDto);
    //ToDoEntryResponseDto updateToDoList(ToDoEntryRequestDto entryRequestDto);
    ToDoEntryResponseDto searchToDoList(ToDoEntryRequestDto entryRequestDto);


    Object getToDoEntryById(String id);
    ToDoEntry getToDoEntryByid(String id);

    ToDoEntry getToDoEntryByTitle(String title);

}
