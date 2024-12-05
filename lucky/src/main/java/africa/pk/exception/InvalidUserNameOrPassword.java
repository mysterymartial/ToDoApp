package africa.pk.exception;

public class InvalidUserNameOrPassword extends RuntimeException {
    public InvalidUserNameOrPassword(String message) {
        super(message);
    }
}
