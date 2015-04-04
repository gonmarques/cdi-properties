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

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.byteslounge.cdi.test.configuration.TestConstants;
import com.byteslounge.cdi.test.it.common.IntegrationTestDeploymentUtils;
import com.byteslounge.cdi.test.it.common.IntegrationTestDeploymentUtils.DeploymentClassAppenderFactory;
import com.byteslounge.cdi.test.utils.MessageBundleUtils;

/**
 * Integration Test
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@RunWith(Arquillian.class)
public class WarProvidedPropertyMethodIT {

    @Drone
    private WebDriver browser;

    @FindBy(id = "hello")
    private WebElement hello;

    @FindBy(id = "system")
    private WebElement system;

    @FindBy(id = "other")
    private WebElement other;

    @FindBy(id = "otherabc")
    private WebElement otherAbc;

    @Deployment
    public static Archive<?> createArchive() throws IOException {

        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "cdipropertiestest.war");
        DeploymentClassAppenderFactory.create(webArchive).append("com.byteslounge.cdi.test.common.TestBean")
                .append("com.byteslounge.cdi.test.common.InjectedBean")
                .append("com.byteslounge.cdi.test.common.ApplicationScopedBean")
                .append("com.byteslounge.cdi.test.common.DependentScopedBean")
                .append("com.byteslounge.cdi.test.wpm.ProvidedPropertyMethodResolver")
                .append("com.byteslounge.cdi.test.wpm.RequestScopedBean")
                .append("com.byteslounge.cdi.test.wpm.SessionScopedBean")
                .append("com.byteslounge.cdi.test.wpm.Service").append("com.byteslounge.cdi.test.wpm.ServiceBean")
                .append("com.byteslounge.cdi.test.model.TestEntity")
                .appendWebXml("src/test/resources/assets/warCommon/WEB-INF/web.xml")
                .appendWebResource("src/test/resources/assets/warCommon/webapp/cditest.xhtml",
                        "src/test/resources/assets/warCommon/webapp/cditestpt.xhtml").appendBeansXml()
                .appendPersistenceXml()
                .appendFacesConfig().appendCDIPropertiesLib().appendLogging().appendProperties()
                .appendOtherProperties();
        IntegrationTestDeploymentUtils.printArchive(webArchive);
        return webArchive;
    }

    @Test
    @RunAsClient
    public void test(@ArquillianResource URL contextPath) {
        browser.get(contextPath + "cditest.xhtml");
        Assert.assertEquals(hello.getText(), MessageBundleUtils.resolveProperty("hello.world", "bl.messages", Locale.getDefault())
                + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(system.getText(), MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", Locale.getDefault(), "Linux", "16")
                + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(other.getText(), MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault())
                + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(otherAbc.getText(),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault(), "B")
                        + TestConstants.PROVIDED_RESOLVER_SUFFIX);

        browser.get(contextPath + "cditestpt.xhtml");
        Assert.assertEquals(hello.getText(), MessageBundleUtils.resolveProperty("hello.world", "bl.messages", new Locale("pt"))
                + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(system.getText(), MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", new Locale("pt"), "Linux", "16")
                + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(other.getText(), MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt"))
                + TestConstants.PROVIDED_RESOLVER_SUFFIX);
        Assert.assertEquals(otherAbc.getText(),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt"), "B")
                        + TestConstants.PROVIDED_RESOLVER_SUFFIX);
    }

}
