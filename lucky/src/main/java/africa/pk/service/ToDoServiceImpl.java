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
import africa.pk.util.ToDoMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToDoServiceImpl implements ToDoServices {
    private static final Logger logger = LoggerFactory.getLogger(ToDoServiceImpl.class);

    @Autowired
    private final ToDoRepository toDoRepository;
    @Autowired
    private final ToDoEntryService toDoEntryService;
    @Autowired
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ToDoResponseDto login(ToDoRequestDto toDoDetails) {
        validateLoginInput(toDoDetails);
        ToDo foundUser = authenticateUser(toDoDetails);
        String token = jwtService.generateToken(foundUser);
        return createLoginResponse(foundUser, token);
    }

    private void validateLoginInput(ToDoRequestDto toDoDetails) {
        if (toDoDetails.getUserName() == null || toDoDetails.getUserName().isBlank()) {
            throw new InvalidInput("Username is required to login");
        }
        if (toDoDetails.getPassword() == null || toDoDetails.getPassword().isBlank()) {
            throw new InvalidInput("Password is required to login");
        }
    }

    private ToDo authenticateUser(ToDoRequestDto toDoDetails) {
        ToDo foundUser = toDoRepository.findByUserName(toDoDetails.getUserName());
        if (foundUser == null || !passwordEncoder.matches(toDoDetails.getPassword(), foundUser.getPassword())) {
            throw new InvalidInput("Invalid username or password");
        }
        return foundUser;
    }

    private ToDoResponseDto createLoginResponse(ToDo user, String token) {
        ToDoResponseDto response = ToDoMapper.toDoResponseDto(user);
        response.setToken(token);
        response.setIsLocked("false");
        response.setMessage("Login successful");
        response.setActivities(user.getActivities());
        return response;
    }

    @Override
    public ToDoResponseDto register(ToDoRequestDto toDoDetails) {
        validateRegistrationInput(toDoDetails);
        ToDo newUser = createNewUser(toDoDetails);
        ToDo savedUser = toDoRepository.save(newUser);
        String token = jwtService.generateToken(savedUser);
        return createRegistrationResponse(savedUser, token);
    }

    private void validateRegistrationInput(ToDoRequestDto toDoDetails) {
        if (toDoDetails.getUserName() == null || toDoDetails.getUserName().isBlank()) {
            throw new InvalidInput("Username cannot be blank");
        }
        if (toDoDetails.getEmail() == null || !toDoDetails.getEmail().contains("@")) {
            throw new InvalidInput("Invalid email address");
        }
        if (toDoRepository.findByUserName(toDoDetails.getUserName()) != null) {
            throw new DuplicateExpection("User with username already exists");
        }
    }

    private ToDo createNewUser(ToDoRequestDto toDoDetails) {
        ToDo newUser = ToDoMapper.toToDo(toDoDetails);
        newUser.setPassword(passwordEncoder.encode(toDoDetails.getPassword()));
        newUser.setActivities(new ArrayList<>());
        return newUser;
    }

    private ToDoResponseDto createRegistrationResponse(ToDo user, String token) {
        ToDoResponseDto response = ToDoMapper.toDoResponseDto(user);
        response.setToken(token);
        response.setIsLocked("false");
        response.setMessage("Registration successful");
        return response;
    }

    @Override
    public ToDoResponseDto searchActivity(ToDoEntryRequestDto toDoDetails) {
        logger.info("Searching for activity with title: {}", toDoDetails.getTitle());
        ToDo currentUser = findUserByUsername(toDoDetails.getUserName());

        if (currentUser.getActivities() == null || currentUser.getActivities().isEmpty()) {
            return createActivityResponse(currentUser, "No activities found");
        }

        List<ToDoEntry> matchingActivities = currentUser.getActivities().stream()
                .filter(activity -> activity.getTitle().toLowerCase()
                        .contains(toDoDetails.getTitle().toLowerCase()))
                .collect(Collectors.toList());

        ToDoResponseDto response = createActivityResponse(currentUser,
                matchingActivities.isEmpty() ? "No matching activities found" : "Activities found successfully");
        response.setActivities(matchingActivities);
        return response;
    }

    @Override
    public ToDoResponseDto updateStatus(ToDoEntryRequestDto toDoDetails) {
        logger.info("Updating status for activity ID: {}", toDoDetails.getId());
        ToDo currentUser = findUserByUsername(toDoDetails.getUserName());

        ToDoEntry activityToUpdate = currentUser.getActivities().stream()
                .filter(activity -> activity.getId().equals(toDoDetails.getId()))
                .findFirst()
                .orElseThrow(() -> new InvalidInput("Activity not found"));

        String newStatus = "completed".equals(activityToUpdate.getStatus()) ? "uncompleted" : "completed";
        activityToUpdate.setStatus(newStatus);
        toDoRepository.save(currentUser);

        return createActivityResponse(currentUser, "Status updated successfully");
    }

    @Override
    public ToDoResponseDto deleteActivity(ToDoEntryRequestDto toDoDetails) {
        logger.info("Deleting activity with ID: {}", toDoDetails.getId());
        ToDo currentUser = findUserByUsername(toDoDetails.getUserName());

        if (currentUser.getActivities() == null) {
            currentUser.setActivities(new ArrayList<>());
        }

        boolean removed = currentUser.getActivities().removeIf(activity ->
                activity.getId().equals(toDoDetails.getId()));

        if (!removed) {
            throw new InvalidInput("Activity not found");
        }

        toDoRepository.save(currentUser);
        return createActivityResponse(currentUser, "Activity deleted successfully");
    }

    @Override
    public ToDoResponseDto addActivity(ToDoEntryRequestDto toDoDetails) {
        logger.info("Adding new activity for user: {}", toDoDetails.getUserName());
        ToDo currentUser = findUserByUsername(toDoDetails.getUserName());

        validateActivityInput(toDoDetails);

        ToDoEntry newActivity = new ToDoEntry();
        newActivity.setId(UUID.randomUUID().toString());
        newActivity.setTitle(toDoDetails.getTitle());
        newActivity.setDescription(toDoDetails.getDescription());
        newActivity.setStatus("uncompleted");

        if (currentUser.getActivities() == null) {
            currentUser.setActivities(new ArrayList<>());
        }

        currentUser.getActivities().add(newActivity);
        toDoRepository.save(currentUser);

        return createActivityResponse(currentUser, "Activity added successfully");
    }

    @Override
    public ToDoResponseDto logout() {
        SecurityContextHolder.clearContext();
        ToDoResponseDto response = new ToDoResponseDto();
        response.setMessage("Logout successful");
        response.setToken(null);
        return response;
    }

    @Override
    public ToDo findUserByUsername(String userName) {
        ToDo user = toDoRepository.findByUserName(userName);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    @Override
    public ToDoResponseDto getAllActivities(String userName) {
        logger.info("Fetching all activities for user: {}", userName);
        ToDo user = findUserByUsername(userName);

        if (user.getActivities() == null) {
            user.setActivities(new ArrayList<>());
        }

        ToDoResponseDto response = createActivityResponse(user,
                user.getActivities().isEmpty() ? "No activities found" : "Activities fetched successfully");
        return response;
    }

    private void validateActivityInput(ToDoEntryRequestDto toDoDetails) {
        if (toDoDetails.getTitle() == null || toDoDetails.getTitle().trim().isEmpty()) {
            throw new InvalidInput("Activity title is required");
        }
        if (toDoDetails.getDescription() == null || toDoDetails.getDescription().trim().isEmpty()) {
            throw new InvalidInput("Activity description is required");
        }
    }

    private ToDoResponseDto createActivityResponse(ToDo user, String message) {
        ToDoResponseDto response = new ToDoResponseDto();
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setMessage(message);
        response.setActivities(user.getActivities());
        response.setIsLocked("false");
        return response;
    }
}
