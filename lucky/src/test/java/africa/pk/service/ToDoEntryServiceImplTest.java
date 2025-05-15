package africa.pk.service;

import africa.pk.data.model.ToDo;
import africa.pk.data.model.ToDoEntry;
import africa.pk.data.repository.ToDoEntryRepository;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.exception.InvalidInput;
import africa.pk.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToDoEntryServiceImplTest {

    @Mock
    private ToDoEntryRepository toDoEntryRepository;

    @InjectMocks
    private ToDoEntryServiceImpl toDoEntryService;

    private ToDoEntryRequestDto validRequestDto;
    private ToDoEntry validEntry;
    private ToDo validUser;

    @BeforeEach
    void setUp() {
        validRequestDto = new ToDoEntryRequestDto();
        validRequestDto.setTitle("Test Todo");
        validRequestDto.setDescription("Test Description");
        validRequestDto.setStatus("uncompleted");

        validEntry = new ToDoEntry();
        validEntry.setId("test-id");
        validEntry.setTitle("Test Todo");
        validEntry.setDescription("Test Description");
        validEntry.setStatus("uncompleted");

        validUser = new ToDo();
        validUser.setUserName("testUser");
    }

    @Test
    void createToDoList_ValidRequest_ReturnsResponseDto() {
        when(toDoEntryRepository.save(any(ToDoEntry.class))).thenReturn(validEntry);

        ToDoEntryResponseDto response = toDoEntryService.createToDoList(validRequestDto);

        assertNotNull(response);
        assertEquals(validEntry.getTitle(), response.getTitle());
        verify(toDoEntryRepository).save(any(ToDoEntry.class));
    }

    @Test
    void deleteToDoList_ValidId_ReturnsResponseDto() {
        when(toDoEntryRepository.findByid("test-id")).thenReturn(validEntry);

        validRequestDto.setId("test-id");
        ToDoEntryResponseDto response = toDoEntryService.deleteToDoList(validRequestDto);

        assertNotNull(response);
        assertEquals(validEntry.getTitle(), response.getTitle());
        verify(toDoEntryRepository).delete(validEntry);
    }

    @Test
    void searchToDoList_ValidTitle_ReturnsResponseDto() {
        when(toDoEntryRepository.findByTitle("Test Todo")).thenReturn(validEntry);

        ToDoEntryResponseDto response = toDoEntryService.searchToDoList(validRequestDto);

        assertNotNull(response);
        assertEquals(validEntry.getTitle(), response.getTitle());
    }

    @Test
    void getToDoEntryById_ValidId_ReturnsEntry() {
        when(toDoEntryRepository.findById("test-id")).thenReturn(Optional.of(validEntry));

        Optional<ToDoEntry> result = toDoEntryService.getToDoEntryById("test-id");

        assertTrue(result.isPresent());
        assertEquals(validEntry.getId(), result.get().getId());
    }

    @Test
    void findUserByUsername_ValidUsername_ReturnsUser() {
        when(toDoEntryRepository.findUserByUserName("testUser"))
                .thenReturn(Optional.of(validUser));

        ToDo result = toDoEntryService.findUserByUsername("testUser");

        assertNotNull(result);
        assertEquals(validUser.getUserName(), result.getUserName());
    }

    @Test
    void createToDoList_NullTitle_ThrowsInvalidInput() {
        validRequestDto.setTitle(null);

        assertThrows(InvalidInput.class, () ->
                toDoEntryService.createToDoList(validRequestDto));
    }

    @Test
    void findUserByUsername_NonexistentUser_ThrowsUserNotFoundException() {
        when(toDoEntryRepository.findUserByUserName("nonexistent"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                toDoEntryService.findUserByUsername("nonexistent"));
    }

    @Test
    void getToDoEntryByTitle_ValidTitle_ReturnsEntry() {
        when(toDoEntryRepository.findByTitle("Test Todo")).thenReturn(validEntry);

        ToDoEntry result = toDoEntryService.getToDoEntryByTitle("Test Todo");

        assertNotNull(result);
        assertEquals(validEntry.getTitle(), result.getTitle());
    }
}
