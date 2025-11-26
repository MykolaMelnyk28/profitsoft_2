package com.melnyk.profitsoft_2.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public final class URIUtil {
    private URIUtil() {}

    public static URI createLocationUri(UriComponentsBuilder uriBuilder, Long id) {
        return uriBuilder
            .path("/api/genres/{id}")
            .build(id);
    }

}
