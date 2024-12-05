package africa.pk.dto.request;
import lombok.Data;
@Data
public class ToDoEntryRequestDto {
    private String id;
    private String title;
    private String description;
    private String status;
}
