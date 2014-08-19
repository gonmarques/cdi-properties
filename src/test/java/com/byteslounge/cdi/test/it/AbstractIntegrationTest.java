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
package com.byteslounge.cdi.test.it;

import java.io.File;

import org.junit.Assert;

import com.byteslounge.cdi.test.configuration.TestConstants;

/**
 * Used in CDI Properties integration tests. See WarDefaultMethodIT.java,
 * WarProvidedMethodIT.java, EjbDefaultMethodIT.java and
 * EjbProvidedMethodIT.java.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
public abstract class AbstractIntegrationTest {

    protected static void checkPreRequisites() {
        checkFileExists(TestConstants.SLF4J_API_JAR);
        checkFileExists(TestConstants.SLF4J_JDK_IMPL_JAR);
    }

    private static void checkFileExists(String filename) {
        File file = new File(filename);
        Assert.assertTrue("File " + filename + " must exist in the project root", file.exists());
    }

}
