package com.melnyk.profitsoft_2;

import com.melnyk.profitsoft_2.util.ResourceUtil;

import java.nio.file.Path;

public final class TestConstants {
    private TestConstants() {}

    public static final String LIQUIBASE_DISABLE_PROFILE = "liquibase_disable";
    public static final Path DEFAULT_DATA_DIR = ResourceUtil.getResourcePath("data");

}
