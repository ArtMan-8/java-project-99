package hexlet.code.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Access denied");
        error.put("message", "You don't have permission to perform this action");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation failed");
        error.put("message", "Invalid input data provided");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Data integrity violation");
        error.put("message", "The operation violates data integrity constraints");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserHasTasksException.class)
    public ResponseEntity<Map<String, String>> handleUserHasTasksException(UserHasTasksException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Cannot delete user");
        error.put("message", "User has assigned tasks and cannot be deleted");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(StatusHasTasksException.class)
    public ResponseEntity<Map<String, String>> handleStatusHasTasksException(StatusHasTasksException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Cannot delete task status");
        error.put("message", "Task status has associated tasks and cannot be deleted");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(LabelHasTasksException.class)
    public ResponseEntity<Map<String, String>> handleLabelHasTasksException(LabelHasTasksException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Cannot delete label");
        error.put("message", "Label has associated tasks and cannot be deleted");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
