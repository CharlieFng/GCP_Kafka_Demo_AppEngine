package club.charliefeng.kafkademoappengine.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class RestUtils {

    public static <T> ResponseEntity OptNotFound(Optional<T> t) {
        if(t.isPresent()) return new ResponseEntity<>(t.get(), HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<T> OK() {return new ResponseEntity<>(HttpStatus.OK);}

    public static <T> ResponseEntity<T> OK(T t) {return new ResponseEntity<>(t, HttpStatus.OK);}

    public static <T> ResponseEntity<T> CREATED() {return new ResponseEntity<>(HttpStatus.CREATED);}

    public static <T> ResponseEntity<T> CREATED(T t) {return new ResponseEntity<>(t, HttpStatus.CREATED);}
}
