package club.charliefeng.kafkademoappengine.exception;

import club.charliefeng.kafkademoappengine.error.ApiError;

public class ValidationException extends RuntimeException {

    private ApiError error;

    public ValidationException(ApiError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ApiError getError() {
        return error;
    }
}
