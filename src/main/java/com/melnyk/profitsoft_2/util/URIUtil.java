package com.melnyk.profitsoft_2.util;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Utility class for working with URIs.
 *
 * <p>All methods are static and this class should not be instantiated.
 */
public final class URIUtil {
    private URIUtil() {}

    /**
     * Builds value for Location header
     * @param uriBuilder uri builder
     * @param resourcePath path to resource base (for example /api/books)
     * @param id resource id
     * @return built {@code URI} object
     */
    public static URI createLocationUri(UriComponentsBuilder uriBuilder, String resourcePath, Long id) {
        return uriBuilder
            .path(resourcePath)
            .path("/{id}")
            .build(id);
    }

}
