package com.melnyk.profitsoft_2.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEventKafkaPublisher {

    private final KafkaTemplate<String, BookEvent> kafkaTemplate;

    @Value("${kafka.topics.books}")
    private String booksTopic;

    @Async
    @TransactionalEventListener(
        classes = BookEvent.class,
        phase = TransactionPhase.AFTER_COMMIT
    )
    public void on(BookEvent event) {
        try {
            String id = String.valueOf(event.data().getId());
            log.debug("Sending book event to kafka {}", id);
            kafkaTemplate.send(booksTopic, id, event);
            log.info("Sent kafka message {}", event);
        } catch (Exception e) {
            log.warn("Sending kafka message fail. ", e);
        }
    }

}
