package pl.michalzadrozny.asweek3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class AsWeek3Application {

    public static void main(String[] args) {
        SpringApplication.run(AsWeek3Application.class, args);
    }


}
