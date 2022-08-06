package exceptions;

public class SystemError extends Exception{
    public enum ErrorType{
        configFileError("حدث خطئ فى تحنيل بانات الرنامج" ),
        printerError("برجاء مراجه اعدادات الطبعه");
        String description;

        ErrorType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
    private final ErrorType errorType;

    public SystemError( ErrorType errorType) {
        super(errorType.description+ " System will shutdown ...");
        this.errorType = errorType;
    }
}
