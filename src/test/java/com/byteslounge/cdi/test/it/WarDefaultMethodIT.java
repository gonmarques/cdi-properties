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

import com.byteslounge.cdi.test.common.InjectedBean;
import com.byteslounge.cdi.test.common.TestBean;
import com.byteslounge.cdi.test.configuration.TestConstants;
import com.byteslounge.cdi.test.configuration.TestProperties;
import com.byteslounge.cdi.test.utils.MessageBundleUtils;
import com.thoughtworks.selenium.DefaultSelenium;

/**
 * Integration test covering the following scenario:
 * 
 * JSF web application where properties are injected into a CDI RequestScoped
 * bean.
 * 
 * The property resolver method is the extension's default.
 * 
 * Two distinct views, each one with its own Locale, are used to test the
 * property resolver.
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@RunWith(Arquillian.class)
public class WarDefaultMethodIT extends AbstractIntegrationTest {

    @Drone
    private DefaultSelenium client;

    @Deployment
    public static Archive<?> createArchive() {

        checkPreRequisites();

        Archive<?> webArchive = ShrinkWrap.create(WebArchive.class, "cdipropertiestest.war").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/test/resources/assets/common/WEB-INF/faces-config.xml"))
                .addAsLibrary(new File("target/cdi-properties-" + TestProperties.instance().getProperty(TestConstants.PROJECT_VERSION) + ".jar"))
                .addAsLibrary(new File(TestConstants.SLF4J_API_JAR)).addAsLibrary(new File(TestConstants.SLF4J_JDK_IMPL_JAR))
                .addAsWebInfResource(new File("src/test/resources/bl/messages.properties"), "classes/bl/messages.properties")
                .addAsWebInfResource(new File("src/test/resources/bl/messages_pt.properties"), "classes/bl/messages_pt.properties")
                .addAsWebInfResource(new File("src/test/resources/bl/other.properties"), "classes/bl/other.properties")
                .addAsWebInfResource(new File("src/test/resources/bl/other_pt.properties"), "classes/bl/other_pt.properties")
                .setWebXML(new File("src/test/resources/assets/warCommon/WEB-INF/web.xml"))
                .addAsWebResource(new File("src/test/resources/assets/warCommon/webapp/cditest.xhtml"))
                .addAsWebResource(new File("src/test/resources/assets/warCommon/webapp/cditestpt.xhtml"))
                .addClasses(TestBean.class, WarDefaultMethodIT.class, InjectedBean.class);
        System.out.println("\n\n" + webArchive.toString(true) + "\n\n");
        return webArchive;
    }

    @Test
    @RunAsClient
    public void test() {
        client.open(TestConstants.TESTING_URL + "/cdipropertiestest/cditest.xhtml");
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'hello')]"),
                MessageBundleUtils.resolveProperty("hello.world", "bl.messages", Locale.getDefault()));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'system')]"),
                MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", Locale.getDefault(), "Linux", "16"));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'other')]"),
                MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault()));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'otherabc')]"),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault(), "B"));

        client.open(TestConstants.TESTING_URL + "/cdipropertiestest/cditestpt.xhtml");
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'hello')]"),
                MessageBundleUtils.resolveProperty("hello.world", "bl.messages", new Locale("pt")));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'system')]"),
                MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", new Locale("pt"), "Linux", "16"));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'other')]"),
                MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt")));
        Assert.assertEquals(client.getText("xpath=//span[contains(@id, 'otherabc')]"),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt"), "B"));
    }

}
