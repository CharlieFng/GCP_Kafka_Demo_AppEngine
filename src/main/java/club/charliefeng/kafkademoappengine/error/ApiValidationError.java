package club.charliefeng.kafkademoappengine.error;

public class ApiValidationError extends ApiSubError {

    private String name;
    private Object value;
    private String type;
    private String message;

    public ApiValidationError(String name, String type) {
        this.name = name;
        this.type = type;
        this.message = String.format("Field %s with its schema type %s cannot be found", name, type);
    }

    public ApiValidationError(String name, Object value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.message = String.format("Field %s with value %s not match its schema type %s", name, value, type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
