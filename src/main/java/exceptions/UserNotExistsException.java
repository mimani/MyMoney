package exceptions;

public class UserNotExistsException extends Exception {
    public UserNotExistsException(String errorMessage) {
        super(errorMessage);
    }
}
