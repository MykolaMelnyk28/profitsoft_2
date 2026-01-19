package com.melnyk.profitsoft_2.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic booksTopic(
        @Value("${kafka.topics.books}") String booksTopic
    ) {
        return TopicBuilder
            .name(booksTopic)
            .build();
    }

}
