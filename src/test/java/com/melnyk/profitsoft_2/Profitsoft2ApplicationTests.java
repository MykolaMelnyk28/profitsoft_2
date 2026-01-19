package com.melnyk.profitsoft_2;

import com.melnyk.profitsoft_2.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(TestConstants.LIQUIBASE_DISABLE_PROFILE)
@Import(TestConfig.class)
@EmbeddedKafka(topics = "${kafka.topics.books}")
class Profitsoft2ApplicationTests {

	@Test
	void contextLoads() {}

}
