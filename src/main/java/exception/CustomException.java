package exception;

public class CustomException extends RuntimeException{
    private final CustomErrorType customErrorType;
    private final int code;
    private final String errorMessage;

    public CustomException(CustomErrorType customErrorType) {
        super(customErrorType.getErrorMessage());
        this.customErrorType = customErrorType;
        this.code = customErrorType.getCode();
        this.errorMessage = customErrorType.getErrorMessage();
    }

    public CustomException(CustomErrorType customErrorType, String errorMessage) {
        super(errorMessage);
        this.customErrorType = customErrorType;
        this.code = customErrorType.getCode();
        this.errorMessage = errorMessage;
    }

    public CustomException(CustomErrorType customErrorType, Exception cause) {
        super(customErrorType.getErrorMessage(), cause);
        this.customErrorType = customErrorType;
        this.code = customErrorType.getCode();
        this.errorMessage = customErrorType.getErrorMessage();
    }

    public CustomException(CustomErrorType customErrorType, int code, String errorMessage) {
        super(customErrorType.getErrorMessage());
        this.customErrorType = customErrorType;
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
