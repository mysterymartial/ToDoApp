package africa.pk.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ToDoResponseDto {
    private int id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
}
