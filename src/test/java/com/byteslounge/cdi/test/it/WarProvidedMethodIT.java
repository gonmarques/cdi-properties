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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.byteslounge.cdi.test.common.ApplicationScopedBean;
import com.byteslounge.cdi.test.common.DependentScopedBean;
import com.byteslounge.cdi.test.common.InjectedBean;
import com.byteslounge.cdi.test.common.TestBean;
import com.byteslounge.cdi.test.configuration.TestConstants;
import com.byteslounge.cdi.test.configuration.TestProperties;
import com.byteslounge.cdi.test.model.TestEntity;
import com.byteslounge.cdi.test.utils.MessageBundleUtils;
import com.byteslounge.cdi.test.wpm.ProvidedMethodResolver;
import com.byteslounge.cdi.test.wpm.RequestScopedBean;
import com.byteslounge.cdi.test.wpm.Service;
import com.byteslounge.cdi.test.wpm.ServiceBean;
import com.byteslounge.cdi.test.wpm.SessionScopedBean;
import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Integration test covering the following scenario:
 * 
 * JSF web application where properties are injected into a CDI RequestScoped
 * bean.
 * 
 * The property resolver method is provided by the application. The provided
 * property resolver method has multiple parameters injected as arguments, each
 * one with its own distinct CDI scope.
 * 
 * An EJB is also injected into the resolver method and we force the EJB to
 * persist a test entity into the underlying data store during property
 * resolving.
 * 
 * Two distinct views, each one with its own Locale, are used to test the
 * property resolver.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@RunWith(Arquillian.class)
public class WarProvidedMethodIT extends AbstractIntegrationTest {

    @Drone
    private DefaultSelenium client;

    @Deployment
    public static Archive<?> createArchive() {

        checkPreRequisites();

        Archive<?> webArchive = ShrinkWrap
                .create(WebArchive.class, "cdipropertiestest.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/test/resources/assets/common/test-persistence.xml"), "classes/META-INF/persistence.xml")
                .addAsWebInfResource(new File("src/test/resources/assets/common/WEB-INF/faces-config.xml"))
                .addAsLibrary(new File("target/cdi-properties-" + TestProperties.instance().getProperty(TestConstants.PROJECT_VERSION) + ".jar"))
                .addAsLibrary(new File(TestConstants.SLF4J_API_JAR))
                .addAsLibrary(new File(TestConstants.SLF4J_JDK_IMPL_JAR))
                .addAsWebInfResource(new File("src/test/resources/bl/messages.properties"), "classes/bl/messages.properties")
                .addAsWebInfResource(new File("src/test/resources/bl/messages_pt.properties"), "classes/bl/messages_pt.properties")
                .addAsWebInfResource(new File("src/test/resources/bl/other.properties"), "classes/bl/other.properties")
                .addAsWebInfResource(new File("src/test/resources/bl/other_pt.properties"), "classes/bl/other_pt.properties")
                .setWebXML(new File("src/test/resources/assets/warCommon/WEB-INF/web.xml"))
                .addAsWebResource(new File("src/test/resources/assets/warCommon/webapp/cditest.xhtml"))
                .addAsWebResource(new File("src/test/resources/assets/warCommon/webapp/cditestpt.xhtml"))
                .addClasses(TestBean.class, WarProvidedMethodIT.class, InjectedBean.class, ApplicationScopedBean.class, DependentScopedBean.class,
                        ProvidedMethodResolver.class, RequestScopedBean.class, SessionScopedBean.class, Service.class, ServiceBean.class, TestEntity.class);
        System.out.println("\n\n" + webArchive.toString(true) + "\n\n");
        return webArchive;
    }

    @Test
    @RunAsClient
    public void test() {
        client.open(TestConstants.TESTING_URL + "/cdipropertiestest/cditest.xhtml");
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'hello')]"),
                MessageBundleUtils.resolveProperty("hello.world", "bl.messages", Locale.getDefault()) + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'system')]"),
                MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", Locale.getDefault(), "Linux", "16")
                        + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'other')]"),
                MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault())
                        + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'otherabc')]"),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault(), "B")
                        + TestConstants.PROVIDED_RESOLVER_SUFFIX);

        client.open(TestConstants.TESTING_URL + "/cdipropertiestest/cditestpt.xhtml");
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'hello')]"),
                MessageBundleUtils.resolveProperty("hello.world", "bl.messages", new Locale("pt")) + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'system')]"),
                MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", new Locale("pt"), "Linux", "16") + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'other')]"),
                MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt"))
                        + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'otherabc')]"),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt"), "B")
                        + TestConstants.PROVIDED_RESOLVER_SUFFIX);
    }

}
