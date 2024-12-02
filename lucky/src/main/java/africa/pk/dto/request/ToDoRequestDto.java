package africa.pk.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ToDoRequestDto {
    private int id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
}
