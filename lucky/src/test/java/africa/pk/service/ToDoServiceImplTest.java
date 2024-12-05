package africa.pk.service;

import africa.pk.data.model.ToDo;
import africa.pk.data.model.ToDoEntry;
import africa.pk.data.repository.ToDoRepository;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.request.ToDoRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.dto.response.ToDoResponseDto;
import africa.pk.exception.AuthorizationExpection;
import africa.pk.exception.InvalidInput;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.verify;


public class ToDoServiceImplTest {
    @Mock
    private ToDoRepository toDoRepository;
    @Mock
    private HttpSession session;
    @Mock
    ToDoEntryService toDoEntryService;
    @InjectMocks
    private ToDoServiceImpl service;

    public ToDoServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("testUser");
        requestDto.setEmail("test@test.com");
        requestDto.setPassword("testPassword");
        ToDo toDo = new ToDo();
        toDo.setUserName("testUser");
        toDo.setEmail("test@test.com");
        toDo.setPassword("testPassword");
        Mockito.when(toDoRepository.save(Mockito.any(ToDo.class))).thenReturn(toDo);
        ToDoResponseDto response = service.register(requestDto);
        assertNotNull(response);
        assertEquals("testUser", response.getUserName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals("testPassword", response.getPassword());
        assertEquals("Registration successful", response.getMessage());
    }

    @Test
    public void testRegister_ShouldThrowException_WhenUserNameExists_WithDifferentEmail() {

        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("existingUser");
        requestDto.setPassword("securePassword");
        requestDto.setEmail("newemail@example.com");


        Mockito.when(toDoRepository.findByUserName("existingUser")).thenReturn(null);


        assertThrows(NullPointerException.class, () -> service.register(requestDto));
    }

    @Test
    public void testRegister_ShouldThrowException_WhenUserNameIsNull() {

        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName(null);
        requestDto.setPassword("securePassword");
        requestDto.setEmail("user@example.com");


        assertThrows(InvalidInput.class, () -> service.register(requestDto));
    }

    @Test
    public void testRegister_ShouldThrowException_WhenEmailIsInvalid() {

        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("newUser");
        requestDto.setPassword("securePassword");
        requestDto.setEmail("invalid-email");

        assertThrows(InvalidInput.class, () -> service.register(requestDto));
    }

    @Test
    public void testRegister_ShouldThrowException_WhenUserNameContainsOnlyWhitespace() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("   ");
        requestDto.setPassword("securePassword");
        requestDto.setEmail("user@example.com");

        assertThrows(InvalidInput.class, () -> service.register(requestDto));


    }

    @Test
    public void testLoginIsSuccessful() {
        ToDoRequestDto toDoRequestDto = new ToDoRequestDto();
        toDoRequestDto.setUserName("validUser");
        toDoRequestDto.setPassword("correctPassword");
        ToDo foundUser = new ToDo();
        foundUser.setUserName("validUser");
        foundUser.setPassword("correctPassword");
        foundUser.setEmail("validEmail@example.com");
        Mockito.when(toDoRepository.findByUserName("validUser")).thenReturn(foundUser);
        ToDoResponseDto response = service.login(toDoRequestDto);

        assertNotNull(response);
        assertEquals("validUser", response.getUserName());
        assertEquals("validEmail@example.com", response.getEmail());


    }

    @Test
    public void testLogin_ShouldThrowException_WhenUsernameIsInvalid() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("invalidUser");
        requestDto.setPassword("correctPassword");

        Mockito.when(toDoRepository.findByUserName("invalidUser")).thenReturn(null);

        assertThrows(InvalidInput.class, () -> service.login(requestDto));
    }

    @Test
    public void testLogin_ShouldThrowException_WhenPasswordIsInvalid() {

        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("validUser");
        requestDto.setPassword("wrongPassword");

        ToDo foundUser = new ToDo();
        foundUser.setUserName("validUser");
        foundUser.setPassword("correctPassword");

        Mockito.when(toDoRepository.findByUserName("validUser")).thenReturn(foundUser);

        assertThrows(InvalidInput.class, () -> service.login(requestDto));
    }

    @Test
    public void testLogin_ShouldReturnUserDetails_WhenCredentialsAreValid() {
        ToDoRequestDto requestDto = new ToDoRequestDto();
        requestDto.setUserName("validUser");
        requestDto.setPassword("correctPassword");

        ToDo foundUser = new ToDo();
        foundUser.setUserName("validUser");
        foundUser.setPassword("correctPassword");
        foundUser.setEmail("user@example.com");

        Mockito.when(toDoRepository.findByUserName("validUser")).thenReturn(foundUser);
        Mockito.when(session.getAttribute("currentUser")).thenReturn(foundUser);

        ToDoResponseDto response = service.login(requestDto);

        assertNotNull(response);
        assertEquals("validUser", response.getUserName());
        assertEquals("user@example.com", response.getEmail());
        verify(session).setAttribute("currentUser", foundUser);
    }

    @Test
    public void testLogout_ShouldLogoutUserSuccessfully() {
        ToDo currentUser = new ToDo();
        currentUser.setUserName("validUser");
        currentUser.setLocked(false);

        Mockito.when(session.getAttribute("currentUser")).thenReturn(currentUser);
        Mockito.when(toDoRepository.save(currentUser)).thenReturn(currentUser);

        ToDoResponseDto response = service.logout();

        assertEquals("validUser", response.getUserName());
        assertEquals("Successfully logged out", response.getMessage());
        verify(session).removeAttribute("currentUser");
    }

    @Test
    public void testLogout_ShouldThrowException_WhenUserIsNotAuthenticated() {
        Mockito.when(session.getAttribute("currentUser")).thenReturn(null);

        assertThrows(AuthorizationExpection.class, () -> service.logout());
    }

    @Test
    void testAddActivity_Success() {

        ToDo mockUser = new ToDo();
        mockUser.setUserName("testUser");
        mockUser.setActivities(new ArrayList<>());

        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setTitle("New Activity");
        requestDto.setDescription("This is a new activity");
        requestDto.setStatus("Pending");

        ToDoEntryResponseDto mockActivity = new ToDoEntryResponseDto();
        mockActivity.setId(mockActivity.getId());
        mockActivity.setTitle("New Activity");
        mockActivity.setDescription("This is a new activity");


        Mockito.when(session.getAttribute("currentUser")).thenReturn(mockUser);
        Mockito.when(toDoEntryService.createToDoList(requestDto)).thenReturn(mockActivity);


        ToDoResponseDto response = service.addActivity(requestDto);


        assertEquals(1, mockUser.getActivities().size());
        assertEquals("New Activity", mockUser.getActivities().get(0).getTitle());
        assertEquals("Activity added successfully", response.getMessage());
        verify(toDoRepository, Mockito.times(1)).save(mockUser);
        verify(toDoEntryService, Mockito.times(1)).createToDoList(requestDto);
    }

    @Test
    void testAddActivity_Unauthorized() {

        Mockito.when(session.getAttribute("currentUser")).thenReturn(null);

        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
        requestDto.setTitle("New Activity");
        requestDto.setDescription("This is a new activity");


        Exception exception = assertThrows(AuthorizationExpection.class, () -> service.addActivity(requestDto));
        assertEquals("User is not authenticated", exception.getMessage());
    }

    @Test
    void testDeleteActivity_Success() {

        ToDo mockUser = new ToDo();
        mockUser.setUserName("testUser");
        mockUser.setActivities(new ArrayList<>());


        ToDoEntry mockActivity = new ToDoEntry();
        mockActivity.setId(mockActivity.getId());
        mockActivity.setTitle("Activity to be deleted");
        mockUser.getActivities().add(mockActivity);


        ToDoEntryRequestDto toDoDetails = new ToDoEntryRequestDto();
        toDoDetails.setId(mockActivity.getId());


        Mockito.when(session.getAttribute("currentUser")).thenReturn(mockUser);
        Mockito.when(toDoEntryService.getToDoEntryById(toDoDetails.getId())).thenReturn(mockActivity);


        ToDoResponseDto response = service.deleteActivity(toDoDetails);

        assertEquals(0, mockUser.getActivities().size());
        assertEquals("Activity deleted successfully", response.getMessage());


        verify(toDoRepository, Mockito.times(1)).save(mockUser);

        verify(toDoEntryService, Mockito.times(1)).getToDoEntryById(toDoDetails.getId());
    }


    @Test
    public void testDeleteActivity_ShouldThrowException_WhenUserNotAuthenticated() {

        Mockito.when(session.getAttribute("currentUser")).thenReturn(null);

        ToDoEntryRequestDto toDoDetails = new ToDoEntryRequestDto();
        toDoDetails.setId(toDoDetails.getId());


        assertThrows(AuthorizationExpection.class, () -> service.deleteActivity(toDoDetails));
    }
    @Test
    void testDeleteActivity_ShouldThrowException_WhenActivityNotFound() {

        ToDo mockUser = new ToDo();
        mockUser.setUserName("testUser");
        mockUser.setActivities(new ArrayList<>());


        ToDoEntryRequestDto toDoDetails = new ToDoEntryRequestDto();
        toDoDetails.setId("99");


        Mockito.when(session.getAttribute("currentUser")).thenReturn(mockUser);

        Mockito.when(toDoEntryService.getToDoEntryById(toDoDetails.getId())).thenReturn(null);


        assertThrows(InvalidInput.class, () -> service.deleteActivity(toDoDetails));
    }
    @Test
    void testSearchActivityByTitle() {
        // Mock the current user session
        ToDo currentUser = new ToDo();
        currentUser.setUserName("testUser");
        Mockito.when(session.getAttribute("currentUser")).thenReturn(currentUser);

        // Mock the ToDoEntryService behavior
        ToDoEntryRequestDto toDoEntryRequestDto = new ToDoEntryRequestDto();
        toDoEntryRequestDto.setTitle("Test Activity");

        ToDoEntry mockEntry = new ToDoEntry();
        mockEntry.setTitle("Test Activity");

        Mockito.when(toDoEntryService.getToDoEntryByTitle("Test Activity")).thenReturn(mockEntry);

        // Call the method
        ToDoResponseDto response = service.searchActivity(toDoEntryRequestDto);

        // Assertions
        assertNotNull(response);
        assertEquals("testUser", response.getUserName());
        assertEquals("Activity found successfully", response.getMessage());
        assertNotNull(response.getActivities());
        assertEquals(1, response.getActivities().size());
        assertEquals("Test Activity", response.getActivities().get(0).getTitle());
    }
//    @Test
//    public void testUpdateActivity_Success() {
//        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
//        requestDto.setId(1);
//        requestDto.setTitle("Updated Title");
//        requestDto.setDescription("Updated Description");
//        requestDto.setStatus("In Progress");
//        requestDto.setDueDate(LocalDate.now().plusDays(1));
//
//
//        ToDo mockUser = new ToDo();
//        mockUser.setUserName("testUser");
//
//        ToDoEntry existingActivity = new ToDoEntry();
//        existingActivity.setId(1);
//        existingActivity.setTitle("Old Title");
//        existingActivity.setDescription("Old Description");
//        existingActivity.setStatus("Pending");
//        existingActivity.setDueDate(LocalDate.now());
//
//        List<ToDoEntry> activities = new ArrayList<>();
//        activities.add(existingActivity);
//        mockUser.setActivities(activities);
//
//
//        Mockito.when(session.getAttribute("currentUser")).thenReturn(mockUser);  // Mock session returning current user
//        Mockito.when(toDoEntryService.getToDoEntryById(requestDto.getId())).thenReturn(existingActivity);  // Mock fetching the correct activity
//        Mockito.when(toDoRepository.save(mockUser)).thenReturn(mockUser);  // Mock saving the updated user
//
//
//        ToDoResponseDto response = service.updateActivity(requestDto);
//
//
//        assertNotNull(response);
//        assertEquals("testUser", response.getUserName());
//        assertEquals("Activity updated successfully", response.getMessage());
//
//
//        assertEquals("Updated Title", existingActivity.getTitle());
//        assertEquals("Updated Description", existingActivity.getDescription());
//        assertEquals("In Progress", existingActivity.getStatus());
//        assertEquals(requestDto.getDueDate(), existingActivity.getDueDate());
//
//
//
//        Mockito.when(toDoEntryService.getToDoEntryById(1)).thenReturn(existingActivity);
//        Mockito.verify(toDoEntryService).getToDoEntryById(requestDto.getId());
//        Mockito.verify(toDoRepository).save(mockUser);
//    }
//    @Test
//    public void testUpdateActivity_ShouldThrowAuthorizationException_WhenUserIsNotAuthenticated() {
//        Mockito.when(session.getAttribute("currentUser")).thenReturn(null);
//
//        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
//        assertThrows(AuthorizationExpection.class, () -> service.updateActivity(requestDto));
//    }
//    @Test
//    public void testUpdateActivity_ShouldThrowInvalidInput_WhenActivityNotFound() {
//        //ToDo mockUser = new ToDo();
//        mockUser.setUserName("testUser");
//
//        Mockito.when(session.getAttribute("currentUser")).thenReturn(mockUser);

//        ToDoEntryRequestDto requestDto = new ToDoEntryRequestDto();
//        requestDto.setId(1); // Activity ID doesn't exist
//
//        assertThrows(InvalidInput.class, () -> service.updateActivity(requestDto));
//    }

}





