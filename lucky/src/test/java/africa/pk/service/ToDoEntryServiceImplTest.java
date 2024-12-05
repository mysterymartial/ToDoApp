package africa.pk.service;

import africa.pk.data.model.ToDoEntry;
import africa.pk.data.repository.ToDoEntryRepository;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.exception.InvalidInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ToDoEntryServiceImplTest {
    @Mock
    private ToDoEntryRepository toDoEntryRepository;
    @InjectMocks
    private ToDoEntryServiceImpl service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateToDoList() {
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setTitle("title");
        requestDto.setDescription("description");
        requestDto.setStatus("pending");

        ToDoEntry savedEntry = new ToDoEntry();
        savedEntry.setId(savedEntry.getId());
        savedEntry.setTitle("title");
        savedEntry.setDescription("description");
        savedEntry.setStatus("pending");
        Mockito.when(toDoEntryRepository.save(Mockito.any(ToDoEntry.class))).thenReturn(savedEntry);
        ToDoEntryResponseDto responseDto = service.createToDoList(requestDto);
        assertNotNull(responseDto);

        assertEquals("title", responseDto.getTitle());

        Mockito.verify(toDoEntryRepository).save(Mockito.any(ToDoEntry.class));
    }

    @Test
    public void testCreateToDoList_ShouldThrowException_WhenTitleIsNull() {
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setDescription("Valid Description");
        requestDto.setStatus("Pending");

        Exception exception = assertThrows(InvalidInput.class, () -> service.createToDoList(requestDto));
        assertEquals("Title cannot be null or blank", exception.getMessage());
    }

    @Test
    public void testCreateToDoList_ShouldThrowException_WhenDescriptionIsNull() {
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setTitle("Valid Title");
        requestDto.setStatus("Pending");

        Exception exception = assertThrows(InvalidInput.class, () -> service.createToDoList(requestDto));
        assertEquals("Description cannot be null or blank", exception.getMessage());
    }

    @Test
    public void testDeleteToDoListIsSuccesfully() {
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setId(requestDto.getId());

        ToDoEntry entry = new ToDoEntry();
        entry.setId(entry.getId());
        entry.setTitle("title");
        Mockito.when(toDoEntryRepository.findByid(entry.getId())).thenReturn(entry);

        ToDoEntryResponseDto responseDto = service.deleteToDoList(requestDto);
        assertNotNull(responseDto);
        assertEquals("title", responseDto.getTitle());
        Mockito.verify(toDoEntryRepository).delete(entry);

    }

    @Test
    public void testDeleteToDoList_ShouldThrowException_WhenIdIsInvalid() {
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setId("-1");

        Exception exception = assertThrows(InvalidInput.class, () -> service.deleteToDoList(requestDto));
        assertEquals("Invalid ID provided", exception.getMessage());
    }

//    @Test
//    public void testUpdateToDoListFunctionality() {
//        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
//        requestDto.setId(requestDto.getId());
//        requestDto.setTitle("updatedTitle");
//        requestDto.setDescription("updatedDescription");
//        requestDto.setStatus("updatedStatus");
//        requestDto.setStatus("completed");
//
//        ToDoEntry entry = new ToDoEntry();
//        entry.setId(entry.getId());
//        entry.setTitle("Title");
//        entry.setDueDate(entry.getDueDate());
//        Mockito.when(toDoEntryRepository.findByid(entry.getId())).thenReturn(entry);
//        Mockito.when(toDoEntryRepository.save(Mockito.any(ToDoEntry.class))).thenReturn(entry);
//
//        ToDoEntryResponseDto response = service.updateToDoList(requestDto);
//        assertNotNull(response);
//        assertEquals("updatedTitle", response.getTitle());
//        assertEquals("completed", response.getStatus());
//        Mockito.verify(toDoEntryRepository).save(entry);
//    }
//
//    @Test
//    public void testUpdateToDoList_ShouldThrowException_WhenIdIsInvalid() {
//        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
//        requestDto.setId("-1");
//        requestDto.setTitle("Updated Title");
//        requestDto.setDescription("Updated Description");
//        requestDto.setStatus("In Progress");
//        assertThrows(InvalidInput.class, () -> service.updateToDoList(requestDto), "Invalid ID provided");
//    }

    @Test
    public void testSearchToDoListFunctionality() {
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setId(requestDto.getId());
        requestDto.setTitle("Title");

        ToDoEntry entry = new ToDoEntry();
        entry.setId(entry.getId());
        entry.setTitle("Title");
        Mockito.when(toDoEntryRepository.findByTitle("Title")).thenReturn(entry);
        ToDoEntryResponseDto response = service.searchToDoList(requestDto);
        assertNotNull(response);
        assertEquals("Title", response.getTitle());
    }

    @Test
    public void testSearchToDoList_ShouldThrowException_WhenRequestDtoIsNull() {

        assertThrows(InvalidInput.class,
                () -> service.searchToDoList(null),
                "Invalid request");
    }

    @Test
    public void testSearchToDoList_ShouldThrowException_WhenTitleAndDescriptionAreMissing() {
        // Arrange
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setId(requestDto.getId()); // Not searching by ID
        requestDto.setTitle(null); // Missing Title
        requestDto.setDescription(null); // Missing Description

        // Act & Assert
        assertThrows(InvalidInput.class,
                () -> service.searchToDoList(requestDto),
                "At least one search parameter (ID, Title, or Description) must be provided");


    }
}