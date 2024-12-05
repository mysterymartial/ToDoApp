package africa.pk.dto.request;

import africa.pk.data.model.ToDoEntry;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ToDoRequestDto {
    private String userName;
    private String password;
    private String email;
    private String id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
    private List<ToDoEntry> activities = new ArrayList<>();

}

