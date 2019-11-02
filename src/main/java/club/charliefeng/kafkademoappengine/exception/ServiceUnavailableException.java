package club.charliefeng.kafkademoappengine.exception;

public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {super(message);}

    public ServiceUnavailableException(String message, Throwable ex) {super(message, ex);}
}
