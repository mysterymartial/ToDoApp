package africa.pk.service;

import africa.pk.data.model.ToDo;
import africa.pk.data.model.ToDoEntry;
import africa.pk.data.repository.ToDoRepository;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.request.ToDoRequestDto;
import africa.pk.dto.response.ToDoEntryResponseDto;
import africa.pk.dto.response.ToDoResponseDto;
import africa.pk.exception.AuthorizationExpection;
import africa.pk.exception.DuplicateExpection;
import africa.pk.exception.InvalidInput;
import africa.pk.exception.UserNotFoundException;
import africa.pk.util.ToDoEntryMapper;
import africa.pk.util.ToDoMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;


@Service
public class ToDoServiceImpl implements ToDoServices{
    @Autowired
    private ToDoRepository toDoRepository;
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private ToDoEntryService toDoEntryService;

    @Override
    public ToDoResponseDto login(ToDoRequestDto toDoDetails) {
        if(toDoDetails.getUserName() == null || toDoDetails.getUserName().isBlank()){
            throw new InvalidInput("Username is required to login");
        }
        if(toDoDetails.getPassword() == null || toDoDetails.getPassword().isBlank()){
            throw new InvalidInput("Password is required to login");
        }
        ToDo foundUser = toDoRepository.findByUserName(toDoDetails.getUserName());
        if(foundUser == null || !foundUser.getPassword().equals(toDoDetails.getPassword())){
            throw new InvalidInput("invalid username or password");
        }
        httpSession.setAttribute("currentUser", foundUser);
        ToDoResponseDto response = ToDoMapper.toDoResponseDto(foundUser);
        response.setIsLocked("false");
        if(response.getIsLocked().equals(true)){
            throw new AuthorizationExpection("you are logged out");

        }



        return response;
    }

    @Override
    public ToDoResponseDto register(ToDoRequestDto toDoDetails) {
        if(toDoDetails.getUserName()== null || toDoDetails.getUserName().isBlank()){
            throw  new InvalidInput("username cannot be blank");
        }
        if (toDoDetails.getEmail() == null || !toDoDetails.getEmail().contains("@")) {
            throw new InvalidInput("Invalid email address");
        }
        ToDo userExist = toDoRepository.findByUserName(toDoDetails.getUserName());
        if(userExist != null) {
            throw new DuplicateExpection("user with username already exist");
        }
        ToDo newUser = ToDoMapper.toToDo(toDoDetails);
        newUser.setActivities(new ArrayList<>());
        ToDo savedUser = toDoRepository.save(newUser);
        httpSession.setAttribute("currentUser", savedUser);
        ToDoResponseDto response = ToDoMapper.toDoResponseDto(savedUser);
        response.setIsLocked("false");
        response.setMessage("Registration successful");

        return response;

    }

    @Override
    public ToDoResponseDto logout() {
        ToDo currentUser = (ToDo) httpSession.getAttribute("currentUser");
        if(currentUser == null) {
            throw new AuthorizationExpection("current user not  available");
        }
        if(currentUser.isLocked()){
            throw new AuthorizationExpection("you are logged out");
        }
        currentUser.setLocked(true);
        httpSession.removeAttribute("currentUser");
        ToDoResponseDto toDoResponseDto = new ToDoResponseDto();
        toDoResponseDto.setUserName(currentUser.getUserName());
        toDoResponseDto.setMessage("Successfully logged out");
        return toDoResponseDto;
    }

    @Override
    public ToDoResponseDto addActivity(ToDoEntryRequestDto toDoDetails) {
        ToDo currentUser = (ToDo) httpSession.getAttribute("currentUser");
        if (currentUser == null) {
            throw new AuthorizationExpection("User is not authenticated");
        }
        if (toDoDetails == null) {
            throw new InvalidInput("Invalid request");
        }

        ToDoEntryResponseDto dtoResponse = toDoEntryService.createToDoList(toDoDetails);


        ToDoEntry newActivity = toToDoEntry(dtoResponse);


        currentUser.getActivities().add(newActivity);


        toDoRepository.save(currentUser);

        // Prepare and return the response
        ToDoResponseDto response = new ToDoResponseDto();
        response.setUserName(currentUser.getUserName());
        response.setMessage("Activity added successfully");

        return response;
    }

    @Override
    public ToDoResponseDto deleteActivity(ToDoEntryRequestDto toDoDetails) {
        ToDo currentUser = (ToDo) httpSession.getAttribute("currentUser");


        if (currentUser == null) {
            throw new AuthorizationExpection("User is not authenticated");
        }


        Object activityToDelete = toDoEntryService.getToDoEntryById(toDoDetails.getId());


        if (activityToDelete == null) {
            throw new InvalidInput("Activity not found");
        }


        currentUser.getActivities().remove(activityToDelete);


        toDoRepository.save(currentUser);


        ToDoResponseDto response = new ToDoResponseDto();
        response.setUserName(currentUser.getUserName());
        response.setMessage("Activity deleted successfully");

        return response;

    }

    @Override
    public ToDoResponseDto updateActivity(ToDoEntryRequestDto toDoDetails) {
        return null;
    }

//    @Override
//    public ToDoResponseDto updateActivity(ToDoEntryRequestDto toDoDetails) {
//        ToDo currentUser = (ToDo) httpSession.getAttribute("currentUser");
//
//        // Validate if the current user exists
//        if (currentUser == null) {
//            throw new AuthorizationExpection("User is not authenticated");
//        }
//
//        // Step 2: Find the activity to be updated using the ID from the request DTO
//        ToDoEntry activityToUpdate = toDoEntryService.getToDoEntryByid(toDoDetails.getId());
//
//        // Step 3: Check if activity exists
//        if (activityToUpdate == null) {
//            throw new InvalidInput("Activity not found");
//        }
//
//        // Step 4: Update the activity fields with the data from the request DTO
//        activityToUpdate.setTitle(toDoDetails.getTitle());
//        activityToUpdate.setDescription(toDoDetails.getDescription());
//        activityToUpdate.setStatus(toDoDetails.getStatus());
//        activityToUpdate.setDueDate(toDoDetails.getDueDate());
//
//        // Step 5: Save the updated activity in the user's activities list
//        toDoRepository.save(currentUser);
//
//        // Step 6: Prepare and return the response DTO
//        ToDoResponseDto response = new ToDoResponseDto();
//        response.setUserName(currentUser.getUserName());
//        response.setMessage("Activity updated successfully");
//
//        // Step 7: Convert activities to response DTOs and include them in the response
//        List<ToDoEntry> activityResponses = currentUser.getActivities().stream()
//                .map(activity -> new ToDoEntry(
//                        activity.getId(),
//                        activity.getTitle(),
//                        activity.getDescription(),
//                        activity.getStatus(),
//                        activity.getDueDate()))
//                .collect(Collectors.toList());
//        response.setActivities(activityResponses);
//
//        return response;
//
//    }

    @Override
    public ToDoResponseDto searchActivity(ToDoEntryRequestDto toDoDetails) {

        if (toDoDetails == null) {
            throw new InvalidInput("Invalid request");
        }

        // Validate if either title or ID is provided
        if ((toDoDetails.getTitle() == null || toDoDetails.getTitle().isBlank())
                && toDoDetails.getId().equals(null)) {
            throw new InvalidInput("Either title or a valid ID must be provided");
        }

        ToDo currentUser = (ToDo) httpSession.getAttribute("currentUser");
        if (currentUser == null) {
            throw new AuthorizationExpection("User is not authenticated");
        }

        ToDoEntry entry = null;

        // Search activity by title
        if (toDoDetails.getTitle() != null && !toDoDetails.getTitle().isBlank()) {
            entry = toDoEntryService.getToDoEntryByTitle(toDoDetails.getTitle());
        }

        // If no title was found, search by ID
        if (entry == null && toDoDetails.getId().equals(null)) {
            entry = toDoEntryService.getToDoEntryByid(toDoDetails.getId());
        }

        // If no entry found, throw an exception
        if (entry == null) {
            throw new InvalidInput("Activity not found with the given title or ID");
        }

        // Return the activity response DTO
        ToDoResponseDto response = new ToDoResponseDto();
        response.setUserName(currentUser.getUserName());
        response.setMessage("Activity found successfully");

        // Map the found entry to a response DTO
        ToDoEntryResponseDto entryResponseDto = ToDoEntryMapper.toDoEntryResponseDto(entry);
        response.setActivities(List.of(entry));

        return response;


        // Other methods...
    }

    @Override
    public ToDo findUserByUsername(String userName) {
        return toDoRepository.findUserByUserName(userName).orElseThrow(()-> new UserNotFoundException("User not found"));
    }


    private ToDoEntry toToDoEntry(ToDoEntryResponseDto dtoResponse) {
        ToDoEntry toDoEntry = new ToDoEntry();
        toDoEntry.setId(dtoResponse.getId());
        toDoEntry.setTitle(dtoResponse.getTitle());
        toDoEntry.setDescription(dtoResponse.getDescription());
        return toDoEntry;
    }
}
