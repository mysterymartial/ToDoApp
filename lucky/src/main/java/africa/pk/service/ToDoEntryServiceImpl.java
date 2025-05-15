package africa.pk.service;

import africa.pk.data.model.ToDo;
import africa.pk.data.model.ToDoEntry;
import africa.pk.data.repository.ToDoEntryRepository;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.exception.InvalidInput;
import africa.pk.exception.UserNotFoundException;
import africa.pk.util.ToDoEntryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToDoEntryServiceImpl implements ToDoEntryService {
    private final ToDoEntryRepository toDoEntryRepository;

    @Override
    public ToDoEntryResponseDto createToDoList(ToDoEntryRequestDto entryRequestDto) {
        validateTodoEntry(entryRequestDto);
        ToDoEntry entry = ToDoEntryMapper.toDoEntry(entryRequestDto);
        entry.setId(UUID.randomUUID().toString());
        entry.setStatus("uncompleted");
        ToDoEntry savedEntry = toDoEntryRepository.save(entry);
        return ToDoEntryMapper.toDoEntryResponseDto(savedEntry);
    }

    @Override
    public ToDoEntryResponseDto deleteToDoList(ToDoEntryRequestDto entryRequestDto) {
        ToDoEntry entry = getEntryById(entryRequestDto.getId());
        toDoEntryRepository.delete(entry);
        return ToDoEntryMapper.toDoEntryResponseDto(entry);
    }

    @Override
    public ToDoEntryResponseDto searchToDoList(ToDoEntryRequestDto entryRequestDto) {
        validateSearchRequest(entryRequestDto);
        ToDoEntry entry = toDoEntryRepository.findByTitle(entryRequestDto.getTitle());
        if (entry == null) {
            throw new InvalidInput("No todo entry found with the given title");
        }
        return ToDoEntryMapper.toDoEntryResponseDto(entry);
    }

    @Override
    public Optional<ToDoEntry> getToDoEntryById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidInput("ID cannot be null or empty");
        }
        return toDoEntryRepository.findById(id);
    }

    @Override
    public ToDoEntry getToDoEntryByid(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidInput("ID cannot be null or empty");
        }
        ToDoEntry entry = toDoEntryRepository.findByid(id);
        if (entry == null) {
            throw new InvalidInput("Todo entry not found with ID: " + id);
        }
        return entry;
    }

    @Override
    public ToDoEntry getToDoEntryByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new InvalidInput("Title cannot be null or empty");
        }
        ToDoEntry entry = toDoEntryRepository.findByTitle(title);
        if (entry == null) {
            throw new InvalidInput("Todo entry not found with title: " + title);
        }
        return entry;
    }

    @Override
    public ToDo findUserByUsername(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new InvalidInput("Username cannot be null or empty");
        }
        return toDoEntryRepository.findUserByUserName(userName)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userName));
    }

    private void validateTodoEntry(ToDoEntryRequestDto entryRequestDto) {
        if (entryRequestDto == null) {
            throw new InvalidInput("Todo entry request cannot be null");
        }
        if (entryRequestDto.getTitle() == null || entryRequestDto.getTitle().isBlank()) {
            throw new InvalidInput("Title is required");
        }
        if (entryRequestDto.getDescription() == null || entryRequestDto.getDescription().isBlank()) {
            throw new InvalidInput("Description is required");
        }
    }

    private void validateSearchRequest(ToDoEntryRequestDto entryRequestDto) {
        if (entryRequestDto == null) {
            throw new InvalidInput("Search request cannot be null");
        }
        if (entryRequestDto.getTitle() == null || entryRequestDto.getTitle().isBlank()) {
            throw new InvalidInput("Search title is required");
        }
    }

    private ToDoEntry getEntryById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidInput("Entry ID is required");
        }
        ToDoEntry entry = toDoEntryRepository.findByid(id);
        if (entry == null) {
            throw new InvalidInput("Todo entry not found with ID: " + id);
        }
        return entry;
    }
}
