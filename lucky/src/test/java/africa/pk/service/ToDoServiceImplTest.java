package africa.pk.service;

import africa.pk.data.model.ToDo;
import africa.pk.data.model.ToDoEntry;
import africa.pk.data.repository.ToDoRepository;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.request.ToDoRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.dto.response.ToDoResponseDto;
import africa.pk.exception.DuplicateExpection;
import africa.pk.exception.InvalidInput;
import africa.pk.exception.UserNotFoundException;
import africa.pk.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToDoServiceImplTest {

    @Mock
    private ToDoRepository toDoRepository;

    @Mock
    private ToDoEntryService toDoEntryService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ToDoServiceImpl toDoService;

    private ToDo testUser;
    private ToDoEntry testActivity;
    private static final String TEST_TOKEN = "test.jwt.token";

    @BeforeEach
    void setUp() {
        testUser = new ToDo();
        testUser.setUserName("testUser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("encodedPassword");
        testUser.setActivities(new ArrayList<>());

        testActivity = new ToDoEntry();
        testActivity.setId(UUID.randomUUID().toString());
        testActivity.setTitle("Test Task");
        testActivity.setDescription("Test Description");
        testActivity.setStatus("uncompleted");
    }

    @Test
    void testSuccessfulRegistration() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("newUser");
        requestDto.setEmail("new@test.com");
        requestDto.setPassword("password");

        when(toDoRepository.findByUserName("newUser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(toDoRepository.save(any(ToDo.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(ToDo.class))).thenReturn(TEST_TOKEN);

        ToDoResponseDto response = toDoService.register(requestDto);

        assertNotNull(response);
        assertEquals("Registration successful", response.getMessage());
        assertEquals(TEST_TOKEN, response.getToken());
        verify(toDoRepository).save(any(ToDo.class));
    }

    @Test
    void testSuccessfulLogin() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setPassword("password");

        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn(TEST_TOKEN);

        ToDoResponseDto response = toDoService.login(requestDto);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals(TEST_TOKEN, response.getToken());
    }

    @Test
    void testAddNewActivity() {
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setTitle("New Task");
        requestDto.setDescription("Task Description");

        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);
        when(toDoRepository.save(any(ToDo.class))).thenReturn(testUser);

        ToDoResponseDto response = toDoService.addActivity(requestDto);

        assertEquals("Activity added successfully", response.getMessage());
        assertFalse(testUser.getActivities().isEmpty());
        verify(toDoRepository).save(testUser);
    }

    @Test
    void testUpdateStatus() {
        testUser.getActivities().add(testActivity);
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setId(testActivity.getId());

        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);
        when(toDoRepository.save(any(ToDo.class))).thenReturn(testUser);

        ToDoResponseDto response = toDoService.updateStatus(requestDto);

        assertEquals("Status updated successfully", response.getMessage());
        verify(toDoRepository).save(testUser);
    }

    @Test
    void testSearchActivity() {
        testUser.getActivities().add(testActivity);
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setTitle("Test");

        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);

        ToDoResponseDto response = toDoService.searchActivity(requestDto);

        assertEquals("Activities found successfully", response.getMessage());
        assertFalse(response.getActivities().isEmpty());
    }

    @Test
    void testDeleteActivity() {
        testUser.getActivities().add(testActivity);
        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setId(testActivity.getId());

        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);
        when(toDoRepository.save(any(ToDo.class))).thenReturn(testUser);

        ToDoResponseDto response = toDoService.deleteActivity(requestDto);

        assertEquals("Activity deleted successfully", response.getMessage());
        assertTrue(testUser.getActivities().isEmpty());
        verify(toDoRepository).save(testUser);
    }

    @Test
    void testGetAllActivities() {
        testUser.getActivities().add(testActivity);
        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);

        ToDoResponseDto response = toDoService.getAllActivities("testUser");

        assertEquals("Activities fetched successfully", response.getMessage());
        assertFalse(response.getActivities().isEmpty());
    }

    @Test
    void testDuplicateRegistration() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("password");

        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);

        assertThrows(DuplicateExpection.class, () -> toDoService.register(requestDto));
    }

    @Test
    void testInvalidLoginCredentials() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setPassword("wrongPassword");

        when(toDoRepository.findByUserName("testUser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidInput.class, () -> toDoService.login(requestDto));
    }
}
