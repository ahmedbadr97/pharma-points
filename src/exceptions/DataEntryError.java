package exceptions;

public class DataEntryError extends Exception {
    String className;
    public DataEntryError(String message) {
        super(message);
    }
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
