package africa.pk.util;

import africa.pk.data.model.ToDoEntry;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
public class ToDoEntryMapper {

    public static ToDoEntry toDoEntry(ToDoEntryRequestDto dtoDetalies){
            ToDoEntry toDoEntry = new ToDoEntry();
            toDoEntry.setTitle(dtoDetalies.getTitle());
            toDoEntry.setDescription(dtoDetalies.getDescription());
            toDoEntry.setStatus(dtoDetalies.getStatus());
            return toDoEntry;
    }

    public static ToDoEntryResponseDto toDoEntryResponseDto(ToDoEntry toDoEntry){
            ToDoEntryResponseDto response = new ToDoEntryResponseDto();
            response.setDescription(toDoEntry.getDescription());
            response.setTitle(toDoEntry.getTitle());
            response.setId(toDoEntry.getId());
            return response;


    }

}
