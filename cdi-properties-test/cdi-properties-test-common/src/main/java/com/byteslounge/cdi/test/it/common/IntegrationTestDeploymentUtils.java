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
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * Utilities class used in Integration Tests deployment setup
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
public class IntegrationTestDeploymentUtils {

    private IntegrationTestDeploymentUtils() {
    }

    public static void addMavenDependencies(LibraryContainer<?> archive, String... dependencies) {
        for (String dependency : dependencies) {
            for (JavaArchive lib : Maven.resolver().resolve(dependency).withTransitivity().as(JavaArchive.class)) {
                archive.addAsLibrary(lib);
            }
        }
    }

    public static void addGlassfishResources(EnterpriseArchive archive) {
        archive.addAsApplicationResource(new File("src/test/resources/glassfish-resources-h2db.xml"),
                "glassfish-resources.xml");
    }

    public static void addGlassfishResources(WebArchive archive) {
        archive.addAsWebInfResource(new File("src/test/resources/glassfish-resources-h2db.xml"),
                "glassfish-resources.xml");
    }

    public static void addJBossResources(EnterpriseArchive archive) {
        archive.addAsApplicationResource(new File("src/test/resources/h2-ds.xml"), "h2-ds.xml");
    }

    public static void addJBossResources(WebArchive archive) {
        archive.addAsWebInfResource(new File("src/test/resources/h2-ds.xml"), "h2-ds.xml");
    }

    public static void printArchive(Archive<?> archive) {
        System.out.println("\n\n" + archive.toString(true) + "\n\n");
    }

    public interface DeploymentAppender<T> {

        DeploymentAppender<T> appendBeansXml();

        DeploymentAppender<T> appendPersistenceXml(ServerType serverType);

        DeploymentAppender<T> appendLogging();

        DeploymentAppender<T> appendProperties();

        DeploymentAppender<T> appendOtherProperties();
    }

    public enum ServerType {

        GLASSFISH("test-persistence.xml"), JBOSS("test-persistence-jboss.xml");

        private final String persistenceXml;

        private ServerType(String persistenceXml) {
            this.persistenceXml = persistenceXml;
        }
    }

    public static class JavaDeploymentAppender implements DeploymentAppender<JavaArchive> {

        private final JavaArchive archive;

        private JavaDeploymentAppender(JavaArchive archive) {
            this.archive = archive;
        }

        @Override
        public JavaDeploymentAppender appendBeansXml() {
            archive.addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml");
            return this;
        }

        @Override
        public JavaDeploymentAppender appendPersistenceXml(ServerType serverType) {
            archive.addAsResource(new File(
"../cdi-properties-test-common/src/test/resources/assets/common/"
                    + serverType.persistenceXml),
                    "META-INF/persistence.xml");
            return this;
        }

        @Override
        public JavaDeploymentAppender appendLogging() {
            archive.addAsResource(new File(
                    "../cdi-properties-test-common/src/test/resources/assets/resources/logging.properties"),
                    "logging.properties");
            return this;
        }

        @Override
        public JavaDeploymentAppender appendProperties() {
            archive.addAsResource(new File("../cdi-properties-test-common/src/test/resources/bl/messages.properties"),
                    "bl/messages.properties").addAsResource(
                    new File("../cdi-properties-test-common/src/test/resources/bl/messages_pt.properties"),
                            "bl/messages_pt.properties");
            return this;
        }

        @Override
        public JavaDeploymentAppender appendOtherProperties() {
            archive.addAsResource(new File("../cdi-properties-test-common/src/test/resources/bl/other.properties"),
                    "bl/other.properties").addAsResource(
                    new File("../cdi-properties-test-common/src/test/resources/bl/other_pt.properties"),
                    "bl/other_pt.properties");
            return this;
        }

        public JavaDeploymentAppender appendCDIPropertiesConfig() {
            archive.addAsResource(new File(
                    "../cdi-properties-test-common/src/test/resources/assets/resources/CDIProperties.properties"),
                    "CDIProperties.properties");
            return this;
        }

    }

    public abstract static class ContainerDeploymentAppender<T extends LibraryContainer<?>> implements DeploymentAppender<T> {

        private final T archive;

        private ContainerDeploymentAppender(T archive) {
            this.archive = archive;
        }

        public ContainerDeploymentAppender<T> appendCDIPropertiesLib() {
            archive.addAsLibrary(new File("../../cdi-properties-main/target/cdi-properties-"
                    + System.getProperty("project.version") + ".jar"));
            return this;
        }

        @Override
        public ContainerDeploymentAppender<T> appendBeansXml() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentAppender<T> appendPersistenceXml(ServerType serverType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentAppender<T> appendLogging() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentAppender<T> appendProperties() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ContainerDeploymentAppender<T> appendOtherProperties() {
            throw new UnsupportedOperationException();
        }

        protected T getArchive() {
            return archive;
        }

    }

    public static class WebDeploymentAppender extends ContainerDeploymentAppender<WebArchive> {

        private WebDeploymentAppender(WebArchive archive) {
            super(archive);

        }

        @Override
        public WebDeploymentAppender appendBeansXml() {
            getArchive().addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
            return this;
        }

        public WebDeploymentAppender appendFacesConfig() {
            getArchive()
                    .addAsWebInfResource(
                            new File(
                                    "../cdi-properties-test-common/src/test/resources/assets/common/WEB-INF/faces-config.xml"));
            return this;
        }

        @Override
        public WebDeploymentAppender appendPersistenceXml(ServerType serverType) {
            getArchive().addAsWebInfResource(
                    new File("../cdi-properties-test-common/src/test/resources/assets/common/"
                            + serverType.persistenceXml),
                    "classes/META-INF/persistence.xml");
            return this;
        }

        @Override
        public WebDeploymentAppender appendLogging() {
            getArchive().addAsWebInfResource(
                    new File("../cdi-properties-test-common/src/test/resources/assets/resources/logging.properties"),
                    "classes/logging.properties");
            return this;
        }

        @Override
        public WebDeploymentAppender appendProperties() {
            getArchive().addAsWebInfResource(
                    new File("../cdi-properties-test-common/src/test/resources/bl/messages.properties"),
                    "classes/bl/messages.properties").addAsWebInfResource(
                    new File("../cdi-properties-test-common/src/test/resources/bl/messages_pt.properties"),
                    "classes/bl/messages_pt.properties");
            return this;
        }

        @Override
        public WebDeploymentAppender appendOtherProperties() {
            getArchive().addAsWebInfResource(
                    new File("../cdi-properties-test-common/src/test/resources/bl/other.properties"),
                    "classes/bl/other.properties").addAsWebInfResource(
                    new File("../cdi-properties-test-common/src/test/resources/bl/other_pt.properties"),
                    "classes/bl/other_pt.properties");
            return this;
        }

        public WebDeploymentAppender appendWebXml(String path) {
            getArchive().setWebXML(new File(path));
            return this;
        }

        public WebDeploymentAppender appendWebResource(String... paths) {
            if (paths == null) {
                throw new IllegalArgumentException();
            }
            for (String path : paths) {
                getArchive().addAsWebResource(new File(path));
            }
            return this;
        }

    }

    public static class EnterpriseDeploymentAppender extends ContainerDeploymentAppender<EnterpriseArchive> {

        private EnterpriseDeploymentAppender(EnterpriseArchive archive) {
            super(archive);
        }

        public EnterpriseDeploymentAppender appendApplicationXml() {
            getArchive().addAsApplicationResource(
                            new File(
                                    "../cdi-properties-test-common/src/test/resources/assets/common/ejbCommon/application.xml"));
            return this;
        }

    }

    public static class DeploymentAppenderFactory {

        private DeploymentAppenderFactory() {
        }

        public static WebDeploymentAppender create(WebArchive archive) {
            return new WebDeploymentAppender(archive);
        }

        public static JavaDeploymentAppender create(JavaArchive archive) {
            return new JavaDeploymentAppender(archive);
        }

        public static EnterpriseDeploymentAppender create(EnterpriseArchive archive) {
            return new EnterpriseDeploymentAppender(archive);
        }
    }

}
