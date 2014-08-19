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
import java.util.Locale;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.byteslounge.cdi.test.common.InjectedBean;
import com.byteslounge.cdi.test.configuration.TestConstants;
import com.byteslounge.cdi.test.configuration.TestProperties;
import com.byteslounge.cdi.test.edm.ServiceEjbDefaultMethod;
import com.byteslounge.cdi.test.edm.ServiceEjbDefaultMethodBean;
import com.byteslounge.cdi.test.edm.TestEjbDefaultBean;
import com.byteslounge.cdi.test.model.TestEntity;
import com.byteslounge.cdi.test.utils.MessageBundleUtils;
import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Integration test covering the following scenario:
 * 
 * EAR containing an EJB module (containing a single Remote EJB) plus the CDI
 * Properties extension as a library. The EJB has properties which are resolved
 * and injected by the extension.
 * 
 * The extension's own default property resolver method is used.
 * 
 * The EJB also persists and reads a test entity into the underlying data store.
 * 
 * The correctness of the results is checked from a JSF web application that
 * fetches the Remote EJB from the EAR's EJB module.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@RunWith(Arquillian.class)
public class EjbDefaultMethodIT extends AbstractIntegrationTest {

    @Drone
    private DefaultSelenium client;

    @Deployment(name = "EAR", order = 1)
    public static Archive<?> createEnterpriseArchive() {

        checkPreRequisites();

        Archive<?> ejbModule = ShrinkWrap.create(JavaArchive.class, "cdipropertiesejb.jar")
                .addClasses(ServiceEjbDefaultMethod.class, ServiceEjbDefaultMethodBean.class, InjectedBean.class, EjbDefaultMethodIT.class, TestEntity.class)
                .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
                .addAsResource(new File("src/test/resources/assets/common/test-persistence.xml"), "META-INF/persistence.xml");
        System.out.println("\n\n" + ejbModule.toString(true) + "\n\n");

        Archive<?> resourcesJar = ShrinkWrap.create(JavaArchive.class, "resources.jar")
                .addAsResource(new File("src/test/resources/bl/messages.properties"), "bl/messages.properties")
                .addAsResource(new File("src/test/resources/bl/other.properties"), "bl/other.properties")
                .addAsResource(new File("src/test/resources/assets/resources/CDIProperties.properties"), "CDIProperties.properties")
                .addAsResource(new File("src/test/resources/assets/resources/logging.properties"), "logging.properties");
        System.out.println("\n\n" + resourcesJar.toString(true) + "\n\n");

        Archive<?> ear = ShrinkWrap.create(EnterpriseArchive.class, "cdiproperties.ear").addAsModule(ejbModule).addAsLibrary(resourcesJar)
                .addAsLibrary(new File("target/cdi-properties-" + TestProperties.instance().getProperty(TestConstants.PROJECT_VERSION) + ".jar"))
                .addAsLibrary(new File(TestConstants.SLF4J_API_JAR)).addAsLibrary(new File(TestConstants.SLF4J_JDK_IMPL_JAR))
                .addAsApplicationResource(new File("src/test/resources/assets/common/ejbCommon/application.xml"));
        System.out.println("\n\n" + ear.toString(true) + "\n\n");
        return ear;
    }

    @Deployment(name = "WAR", order = 2)
    public static Archive<?> createWebArchive() {

        checkPreRequisites();

        Archive<?> webMobule = ShrinkWrap.create(WebArchive.class, "cdipropertiestest.war")
                .setWebXML(new File("src/test/resources/assets/common/ejbCommon/WEB-INF/web.xml")).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/test/resources/assets/common/WEB-INF/faces-config.xml"))
                .addAsWebResource(new File("src/test/resources/assets/common/ejbCommon/webapp/cditestejb.xhtml"))
                .addClasses(TestEjbDefaultBean.class, InjectedBean.class, ServiceEjbDefaultMethod.class).addAsLibrary(new File(TestConstants.SLF4J_API_JAR))
                .addAsLibrary(new File(TestConstants.SLF4J_JDK_IMPL_JAR));
        System.out.println("\n\n" + webMobule.toString(true) + "\n\n");
        return webMobule;
    }

    @Test
    @RunAsClient
    public void test() {
        client.open(TestConstants.TESTING_URL + "/cdipropertiestest/cditestejb.xhtml");
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'hello')]"),
                MessageBundleUtils.resolveProperty("hello.world", "bl.messages", Locale.getDefault()));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'system')]"),
                MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", Locale.getDefault(), "Linux", "16"));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'other')]"),
                MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault()));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'otherabc')]"),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault(), "B"));

    }

}
