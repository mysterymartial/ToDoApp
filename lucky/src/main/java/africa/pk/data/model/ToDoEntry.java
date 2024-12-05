package africa.pk.data.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ToDoEntry {
    @Id
    private String id;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate = LocalDate.now();

}
