package com.melnyk.profitsoft_2.util;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public final class ResourceUtil {
    private ResourceUtil() {}

    public static Path getResourcePath(String resourceName) {
        try {
            return Path.of(
                Objects.requireNonNull(
                    ResourceUtil.class.getClassLoader().getResource(resourceName)
                ).toURI()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
