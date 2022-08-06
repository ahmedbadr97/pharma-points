package exceptions;

public class NoSufficientPrivileges extends Exception{
    public NoSufficientPrivileges() {
        super("فقط المسئول يمكنه الدخول الى هذه الصفحه");
    }
}
