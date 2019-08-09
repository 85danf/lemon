package org.danf.lemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main class of the application, configures most Spring Boot aspects of the application and serves as an entry point.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
@EnableAsync
@SpringBootApplication
public class LemonApplication extends SpringBootServletInitializer implements AsyncConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(LemonApplication.class, args);
	}

}


