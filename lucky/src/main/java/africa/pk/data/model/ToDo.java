package africa.pk.data.model;

import africa.pk.dto.response.ToDoEntryResponseDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class ToDo {
    @Id
    private String id;
    private String userName;
    private String password;
    private String email;
    private boolean isLocked = true;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate = LocalDate.now();
    @DBRef
    private List<ToDoEntry> activities = new ArrayList<>();
}
