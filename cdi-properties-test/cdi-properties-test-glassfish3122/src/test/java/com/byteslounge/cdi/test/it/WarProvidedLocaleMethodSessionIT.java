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

import com.byteslounge.cdi.test.it.common.AbstractWarProvidedLocaleMethodSession;
import com.byteslounge.cdi.test.it.common.IntegrationTestDeploymentUtils;
import com.byteslounge.cdi.test.utils.MessageBundleUtils;

/**
 * Integration Test
 * 
 * @author Gonçalo Marques
 * @since 1.1.0
 */
@RunWith(Arquillian.class)
public class WarProvidedLocaleMethodSessionIT extends AbstractWarProvidedLocaleMethodSession {

    @Drone
    private WebDriver browser;

    @FindBy(id = "result")
    private WebElement result;

    @FindBy(id = "integer")
    private WebElement integer;

    @Deployment
    public static WebArchive createArchive() throws IOException {
        WebArchive webArchive = CommonWarProvidedLocaleMethodSession.createArchive();
        IntegrationTestDeploymentUtils.addMavenDependencies(webArchive,
                "org.slf4j:slf4j-api:" + System.getProperty("slf4j-api.version"),
                "org.slf4j:slf4j-jdk14:" + System.getProperty("slf4j-api.version"));
        return webArchive;
    }

    @Test
    @RunAsClient
    public void test(@ArquillianResource URL contextPath) {
        browser.get(contextPath + "testservlet");
        Assert.assertEquals(result.getText(),
                MessageBundleUtils.resolveProperty("hello.world", "bl.messages", Locale.getDefault()));
        Assert.assertEquals(integer.getText(),
                MessageBundleUtils.resolveProperty("some.integer", "bl.messages", Locale.getDefault()));

    }

}
