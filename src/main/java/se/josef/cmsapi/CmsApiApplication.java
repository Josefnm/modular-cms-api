package se.josef.cmsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//TODO remove exclusions when security is added
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class})
public class CmsApiApplication {

	public static void main(String[] args) {
		System.out.println(System.getenv().toString());
		SpringApplication.run(CmsApiApplication.class, args);
	}

}
