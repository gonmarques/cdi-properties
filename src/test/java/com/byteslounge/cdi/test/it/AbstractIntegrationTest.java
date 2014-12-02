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
import java.io.IOException;

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

    /**
     * Glassfish embedded will also include the classpath of the test
     * application itself.
     * 
     * In order to make sure that the classes deployed through Arquillian are
     * exclusively loaded by the glassfish application classloader, we remove
     * them form the test classpath and move them into an external directory
     * 
     * @throws IOException
     */
    static void prepareClasses() throws IOException {
        File file = new File(TestConstants.TEST_TARGET_CLASSES_DIRECTORY + "/com/byteslounge/cdi/test/common/TestBean.class");
        if (file.exists()) {
            processDirectory("common");
            processDirectory("wpm");
            processDirectory("model");
            processDirectory("edm");
            processDirectory("epm");
        }
    }

    private static void processDirectory(String directory) {
        File targetDir = new File(TestConstants.EXTERNAL_CLASSES_DIRECTORY + "/com/byteslounge/cdi/test/" + directory);
        targetDir.mkdirs();
        for (File file : new File(TestConstants.TEST_TARGET_CLASSES_DIRECTORY + "/com/byteslounge/cdi/test/" + directory).listFiles()) {
            file.renameTo(new File(TestConstants.EXTERNAL_CLASSES_DIRECTORY + "/com/byteslounge/cdi/test/" + directory + "/" + file.getName()));
        }
    }

}
