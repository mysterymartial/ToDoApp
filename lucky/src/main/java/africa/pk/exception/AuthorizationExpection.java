package africa.pk.exception;

public class AuthorizationExpection extends RuntimeException {
    public AuthorizationExpection(String message) {
        super(message);
    }
}
