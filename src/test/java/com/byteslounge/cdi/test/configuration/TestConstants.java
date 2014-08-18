/*
 * Copyright 2014 byteslounge.com (Gonçalo Marques).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.byteslounge.cdi.test.configuration;

/**
 * Used in CDI Properties integration tests. See WarDefaultMethodIT.java,
 * WarProvidedMethodIT.java, EjbDefaultMethodIT.java and
 * EjbProvidedMethodIT.java.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public class TestConstants {

    private TestConstants() {
    }

    public static final String TESTING_URL = "http://localhost:8080/cdipropertiestest";
    public static final String OTHER_RESOURCE_BUNDLE_NAME = "bl.other";
    public static final String SLF4J_API_JAR = "slf4j-api-1.7.5.jar";
    public static final String SLF4J_JDK_IMPL_JAR = "slf4j-jdk14-1.7.7.jar";
    public static final String BEAN_TEST_RETURN_VALUE = "getTextResult";
    public static final String PROVIDED_RESOLVER_SUFFIX = "provided";

}
