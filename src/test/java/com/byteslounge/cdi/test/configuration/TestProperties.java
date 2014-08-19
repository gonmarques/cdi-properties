package com.byteslounge.cdi.test.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.byteslounge.cdi.configuration.ExtensionConfiguration;

public class TestProperties {

    private static final TestProperties INSTANCE = new TestProperties();
    private final Properties properties;

    private TestProperties() {
        URL url = ExtensionConfiguration.class.getClassLoader().getResource(TestConstants.TEST_PROPERTIES_FILE);
        properties = new Properties();
        try {
            properties.load(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize TestProperties", e);
        }
    }

    public static TestProperties instance() {
        return INSTANCE;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
