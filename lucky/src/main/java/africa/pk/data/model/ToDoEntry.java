package africa.pk.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
public class ToDoEntry {
    @Id
    private int id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
}
