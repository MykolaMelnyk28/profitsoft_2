package com.melnyk.profitsoft_2;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(PaginationProps.class)
@EnableCaching
public class Profitsoft2Application {

	public static void main(String[] args) {
		SpringApplication.run(Profitsoft2Application.class, args);
	}

}
