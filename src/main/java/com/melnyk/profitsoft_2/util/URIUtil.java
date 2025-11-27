package com.melnyk.profitsoft_2.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public final class URIUtil {
    private URIUtil() {}

    public static URI createLocationUri(UriComponentsBuilder uriBuilder, String resourcePath, Long id) {
        return uriBuilder
            .path(resourcePath)
            .path("/{id}")
            .build(id);
    }

}
