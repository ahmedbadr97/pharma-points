package exceptions;


public class DataNotFound extends Exception {
    String className;
    public DataNotFound(String message) {
        super(message);
        className="";

    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
