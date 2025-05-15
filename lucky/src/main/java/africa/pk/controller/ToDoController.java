package africa.pk.controller;

import africa.pk.data.model.ApiResponse;
import africa.pk.dto.request.ToDoEntryRequestDto;
import africa.pk.dto.request.ToDoRequestDto;
import africa.pk.dto.response.ToDoResponseDto;
import africa.pk.exception.AuthorizationExpection;
import africa.pk.exception.DuplicateExpection;
import africa.pk.exception.InvalidInput;
import africa.pk.exception.UserNotFoundException;
import africa.pk.service.JwtService;
import africa.pk.service.ToDoServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("api/v1/todo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoServices toDoServices;
    private final JwtService jwtService;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> register(@Valid @RequestBody ToDoRequestDto dtoDetails) {
        validateEmail(dtoDetails.getEmail());
        validatePassword(dtoDetails.getPassword());
        ToDoResponseDto responseDto = toDoServices.register(dtoDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Registration successful", responseDto));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> login(@Valid @RequestBody ToDoRequestDto dtoDetails) {
        ToDoResponseDto responseDto = toDoServices.login(dtoDetails);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", responseDto));
    }

    @GetMapping("/activities")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> getAllActivities(
            @RequestHeader("Authorization") String token) {
        String userName = extractUserNameFromToken(token);
        ToDoResponseDto response = toDoServices.getAllActivities(userName);
        return ResponseEntity.ok(new ApiResponse<>(true, "Activities retrieved successfully", response));
    }

    @GetMapping("/activity/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> searchActivity(
            @RequestParam String query,
            @RequestHeader("Authorization") String token) {
        ToDoEntryRequestDto searchRequest = new ToDoEntryRequestDto();
        searchRequest.setTitle(query);
        searchRequest.setUserName(extractUserNameFromToken(token));
        ToDoResponseDto responseDto = toDoServices.searchActivity(searchRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Search completed", responseDto));
    }

    @PostMapping("/add/activity")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> addActivity(
            @Valid @RequestBody ToDoEntryRequestDto toDoDetails,
            @RequestHeader("Authorization") String token) {
        toDoDetails.setUserName(extractUserNameFromToken(token));
        ToDoResponseDto responseDto = toDoServices.addActivity(toDoDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Activity added successfully", responseDto));
    }

    @DeleteMapping("/activity/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> deleteActivity(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        ToDoEntryRequestDto request = new ToDoEntryRequestDto();
        request.setId(id);
        request.setUserName(extractUserNameFromToken(token));
        ToDoResponseDto responseDto = toDoServices.deleteActivity(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Activity deleted successfully", responseDto));
    }

    @PutMapping("/activity/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> toggleStatus(
            @PathVariable String id,
            @RequestHeader("Authorization") String token) {
        ToDoEntryRequestDto request = new ToDoEntryRequestDto();
        request.setId(id);
        request.setUserName(extractUserNameFromToken(token));
        ToDoResponseDto responseDto = toDoServices.updateStatus(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Status updated successfully", responseDto));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ToDoResponseDto>> logout() {
        ToDoResponseDto responseDto = toDoServices.logout();
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout successful", responseDto));
    }

    private String extractUserNameFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return jwtService.extractUsername(token.substring(7));
        }
        throw new AuthorizationExpection("Invalid token format");
    }

    private void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInput("Invalid email format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new InvalidInput("Password must be at least 6 characters long");
        }
    }

    @ExceptionHandler({InvalidInput.class, DuplicateExpection.class})
    public ResponseEntity<ApiResponse<?>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler({UserNotFoundException.class, AuthorizationExpection.class})
    public ResponseEntity<ApiResponse<?>> handleUnauthorized(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "An unexpected error occurred", null));
    }
}
