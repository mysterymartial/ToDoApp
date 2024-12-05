package africa.pk.dto.response;

import africa.pk.data.model.ToDoEntry;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ToDoResponseDto {
    private String userName;
    private String password;
    private String email;
    private String message;
    private String isLocked;
    private String id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private List<ToDoEntry> activities = new ArrayList<>();

}
