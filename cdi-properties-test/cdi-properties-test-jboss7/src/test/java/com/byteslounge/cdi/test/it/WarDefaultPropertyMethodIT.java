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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.byteslounge.cdi.test.configuration.TestConstants;
import com.byteslounge.cdi.test.it.common.AbstractWarDefaultPropertyMethod;
import com.byteslounge.cdi.test.utils.MessageBundleUtils;

/**
 * Integration Test
 * 
 * @author Gonçalo Marques
 * @since 1.0.0
 */
@RunWith(Arquillian.class)
public class WarDefaultPropertyMethodIT extends AbstractWarDefaultPropertyMethod {

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
    public static WebArchive createArchive() throws IOException {
        WebArchive webArchive = CommonWarDefaultPropertyMethod.createArchive();
        return webArchive;

    }

    @Test
    @RunAsClient
    public void test(@ArquillianResource URL contextPath) {
        browser.get(contextPath + "cditest.xhtml");
        Assert.assertEquals(hello.getText(), MessageBundleUtils.resolveProperty("hello.world", "bl.messages", Locale.getDefault()));
        Assert.assertEquals(system.getText(), MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", Locale.getDefault(), "Linux", "16"));
        Assert.assertEquals(other.getText(), MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault()));
        Assert.assertEquals(otherAbc.getText(),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, Locale.getDefault(), "B"));

        browser.get(contextPath + "cditestpt.xhtml");
        Assert.assertEquals(hello.getText(), MessageBundleUtils.resolveProperty("hello.world", "bl.messages", new Locale("pt")));
        Assert.assertEquals(system.getText(), MessageBundleUtils.resolveProperty("system.linux.box", "bl.messages", new Locale("pt"), "Linux", "16"));
        Assert.assertEquals(other.getText(), MessageBundleUtils.resolveProperty("other.message", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt")));
        Assert.assertEquals(otherAbc.getText(),
                MessageBundleUtils.resolveProperty("other.parameter", TestConstants.OTHER_RESOURCE_BUNDLE_NAME, new Locale("pt"), "B"));
    }

}
