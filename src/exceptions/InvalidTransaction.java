package exceptions;

public class InvalidTransaction extends Exception{
    public InvalidTransaction(String message) {
        super(message);
    }
}
