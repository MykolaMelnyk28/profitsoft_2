package com.melnyk.profitsoft_2.event;

import java.time.Instant;
import java.util.Map;

public interface Event<T> {

    String type();
    T data();
    Instant occurredAt();
    Map<String, Object> properties();

}
