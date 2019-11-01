package club.charliefeng.kafkademoappengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@SpringBootApplication
@RestController
public class KafkaDemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(KafkaDemoApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return "Hello App Engine Standard!";
	}

}
