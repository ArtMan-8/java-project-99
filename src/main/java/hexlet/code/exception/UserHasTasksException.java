package hexlet.code.exception;

public class UserHasTasksException extends RuntimeException {
    public UserHasTasksException(String message) {
        super(message);
    }
}
