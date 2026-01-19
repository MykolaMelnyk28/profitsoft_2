package com.melnyk.profitsoft_2;

import com.melnyk.profitsoft_2.config.props.CorsProps;
import com.melnyk.profitsoft_2.config.props.PaginationProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties({PaginationProps.class, CorsProps.class})
@EnableCaching
@EnableAsync
public class Profitsoft2Application {

	public static void main(String[] args) {
		SpringApplication.run(Profitsoft2Application.class, args);
	}

}
