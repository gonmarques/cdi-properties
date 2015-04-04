/*
 * Copyright 2015 byteslounge.com (Gonçalo Marques).
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
package com.byteslounge.cdi.test.it.common;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.exceptions.UnsupportedOperationException;

import com.byteslounge.cdi.test.configuration.TestConstants;

/**
 * Utilities class used in Integration Tests deployment setup
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class IntegrationTestDeploymentUtils {

    private IntegrationTestDeploymentUtils() {
    }

    /**
     * Glassfish embedded will also include the classpath of the test
     * application itself.
     * 
     * In order to make sure that the classes deployed through Arquillian are
     * exclusively loaded by the glassfish application classloader, we remove
     * them form the test classpath and move them into an external directory
     * 
     */
    private static void prepareClasses() {
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

    public static void printArchive(Archive<?> archive) {
        System.out.println("\n\n" + archive.toString(true) + "\n\n");
    }

    public interface DeploymentClassAppender<T> {
        DeploymentClassAppender<T> append(String className);

        DeploymentClassAppender<T> appendBeansXml();

        DeploymentClassAppender<T> appendPersistenceXml();

        DeploymentClassAppender<T> appendLogging();

        DeploymentClassAppender<T> appendProperties();

        DeploymentClassAppender<T> appendOtherProperties();
    }

    public static class JavaDeploymentClassAppender implements DeploymentClassAppender<JavaArchive> {

        private final JavaArchive archive;

        private JavaDeploymentClassAppender(JavaArchive archive) {
            this.archive = archive;
        }

        @Override
        public JavaDeploymentClassAppender append(String className) {
            String classFilePath = className.replace(".", "/") + ".class";
            archive.addAsResource(new File(TestConstants.EXTERNAL_CLASSES_DIRECTORY + "/" + classFilePath),
                    classFilePath);
            return this;
        }

        @Override
        public JavaDeploymentClassAppender appendBeansXml() {
            archive.addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml");
            return this;
        }

        @Override
        public JavaDeploymentClassAppender appendPersistenceXml() {
            archive.addAsResource(new File("src/test/resources/assets/common/test-persistence.xml"),
                    "META-INF/persistence.xml");
            return this;
        }

        @Override
        public JavaDeploymentClassAppender appendLogging() {
            archive.addAsResource(new File("src/test/resources/assets/resources/logging.properties"),
                    "logging.properties");
            return this;
        }

        @Override
        public JavaDeploymentClassAppender appendProperties() {
            archive.addAsResource(new File("src/test/resources/bl/messages.properties"), "bl/messages.properties")
                    .addAsResource(new File("src/test/resources/bl/messages_pt.properties"),
                            "bl/messages_pt.properties");
            return this;
        }

        @Override
        public JavaDeploymentClassAppender appendOtherProperties() {
            archive.addAsResource(new File("src/test/resources/bl/other.properties"), "bl/other.properties")
                    .addAsResource(new File("src/test/resources/bl/other_pt.properties"), "bl/other_pt.properties");
            return this;
        }

        public JavaDeploymentClassAppender appendCDIPropertiesConfig() {
            archive.addAsResource(new File("src/test/resources/assets/resources/CDIProperties.properties"),
                    "CDIProperties.properties");
            return this;
        }

    }

    public abstract static class ContainerDeploymentClassAppender<T extends LibraryContainer<?>> implements DeploymentClassAppender<T> {

        private final T archive;

        private ContainerDeploymentClassAppender(T archive) {
            this.archive = archive;
        }

        public ContainerDeploymentClassAppender<T> appendCDIPropertiesLib() {
            archive.addAsLibrary(new File("target/cdi-properties-" + System.getProperty("project.version") + ".jar"));
            return this;
        }

        @Override
        public ContainerDeploymentClassAppender<T> append(String className) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentClassAppender<T> appendBeansXml() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentClassAppender<T> appendPersistenceXml() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentClassAppender<T> appendLogging() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentClassAppender<T> appendProperties() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentClassAppender<T> appendOtherProperties() {
            throw new UnsupportedOperationException();
        }

        protected T getArchive() {
            return archive;
        }

    }

    public static class WebDeploymentClassAppender extends ContainerDeploymentClassAppender<WebArchive> {

        private WebDeploymentClassAppender(WebArchive archive) {
            super(archive);

        }

        @Override
        public WebDeploymentClassAppender append(String className) {
            String classFilePath = "/" + className.replace(".", "/") + ".class";
            getArchive().addAsWebInfResource(new File(TestConstants.EXTERNAL_CLASSES_DIRECTORY + classFilePath),
                    "classes"
                    + classFilePath);
            return this;
        }

        @Override
        public WebDeploymentClassAppender appendBeansXml() {
            getArchive().addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
            return this;
        }

        public WebDeploymentClassAppender appendFacesConfig() {
            getArchive().addAsWebInfResource(new File("src/test/resources/assets/common/WEB-INF/faces-config.xml"));
            return this;
        }

        @Override
        public WebDeploymentClassAppender appendPersistenceXml() {
            getArchive().addAsWebInfResource(new File("src/test/resources/assets/common/test-persistence.xml"),
                    "classes/META-INF/persistence.xml");
            return this;
        }

        @Override
        public WebDeploymentClassAppender appendLogging() {
            getArchive().addAsWebInfResource(new File("src/test/resources/assets/resources/logging.properties"),
                    "classes/logging.properties");
            return this;
        }

        @Override
        public WebDeploymentClassAppender appendProperties() {
            getArchive().addAsWebInfResource(new File("src/test/resources/bl/messages.properties"),
                    "classes/bl/messages.properties").addAsWebInfResource(
                    new File("src/test/resources/bl/messages_pt.properties"), "classes/bl/messages_pt.properties");
            return this;
        }

        @Override
        public WebDeploymentClassAppender appendOtherProperties() {
            getArchive().addAsWebInfResource(new File("src/test/resources/bl/other.properties"),
                    "classes/bl/other.properties").addAsWebInfResource(
                    new File("src/test/resources/bl/other_pt.properties"), "classes/bl/other_pt.properties");
            return this;
        }

        public WebDeploymentClassAppender appendWebXml(String path) {
            getArchive().setWebXML(new File(path));
            return this;
        }

        public WebDeploymentClassAppender appendWebResource(String... paths) {
            if (paths == null) {
                throw new IllegalArgumentException();
            }
            for (String path : paths) {
                getArchive().addAsWebResource(new File(path));
            }
            return this;
        }

    }

    public static class EnterpriseDeploymentClassAppender extends ContainerDeploymentClassAppender<EnterpriseArchive> {

        private EnterpriseDeploymentClassAppender(EnterpriseArchive archive) {
            super(archive);
        }

        public EnterpriseDeploymentClassAppender appendApplicationXml() {
            getArchive().addAsApplicationResource(
                    new File("src/test/resources/assets/common/ejbCommon/application.xml"));
            return this;
        }

    }

    public static class DeploymentClassAppenderFactory {

        static {
            IntegrationTestDeploymentUtils.prepareClasses();
        }

        private DeploymentClassAppenderFactory() {
        }

        public static WebDeploymentClassAppender create(WebArchive archive) {
            return new WebDeploymentClassAppender(archive);
        }

        public static JavaDeploymentClassAppender create(JavaArchive archive) {
            return new JavaDeploymentClassAppender(archive);
        }

        public static EnterpriseDeploymentClassAppender create(EnterpriseArchive archive) {
            return new EnterpriseDeploymentClassAppender(archive);
        }
    }

}
