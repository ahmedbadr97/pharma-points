package exceptions;

import java.sql.SQLException;

public class DatabaseError extends SQLException {
    public DatabaseError(String message) {
        super(message);
    }
}
