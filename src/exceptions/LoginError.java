package exceptions;

public class LoginError extends Exception {
    public LoginError(String message) {
        super(message);
    }
}
