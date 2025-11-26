package com.melnyk.profitsoft_2.config;

import com.melnyk.profitsoft_2.TestConstants;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {

    @Bean
    @Primary
    @Profile(TestConstants.LIQUIBASE_DISABLE_PROFILE)
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setShouldRun(false);
        return liquibase;
    }

}
