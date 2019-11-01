package club.charliefeng.kafkademoappengine.servlet;

import club.charliefeng.kafkademoappengine.KafkaDemoApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(KafkaDemoApplication.class);
	}

}
