#CDI Properties

**Note**: CDI Properties includes a set of Arquillian based Integration Tests that cover the most common use case scenarios (from the simplest scenario to others that explore some degree of complexity). Detailed information about the included Integration Tests is available in the last section of this README file.

##Introduction

CDI Properties is a CDI extension that enables resource bundle injection - and optional custom resource bundle resolution - in a CDI enabled Java EE application, requiring little to no configuration depending on the application requirements. The resource bundle source may be the typical `.properties` files or any other external resource or system.

The extension may be used in two distinct Java EE scenarios:

- JSF based web application
- EJB application

The extension takes internationalization into account, meaning that it will resolve the correct resource bundles according to the contextual Locale during resource bundle resolution.

The resolved properties are also converted to the expected type, meaning that if a property is injected into an `Integer` field, the resolved property will be converted to an `Integer` instance.

Feel free to open new issues for bug reporting or enhancement requests.

##Usage

Will retrieve the property `hello.world` from the **default** resource bundle:
```java
@Property("hello.world")
private String helloWorld;
```

Will retrieve the property `hello.world` from the `messages.properties` resource bundle:
```java
@Property(value = "hello.world", resourceBundleBaseName = "messages")
private String helloWorld;
```

One may store the resource bundle names in application constants:
```java
@Property(value = "hello.world", 
  resourceBundleBaseName = ApplicationConstants.MESSAGES_RESOURCE_BUNDLE)
private String helloWorld;

@Property(value = "other.fancy.message", 
  resourceBundleBaseName = ApplicationConstants.ALTERNATIVE_RESOURCE_BUNDLE)
private String otherProperty;
```

Message formatting is also possible:
```java
system.message = The system is running a {0} box with {1} MB of available RAM

@Property(value = "system.message", parameters = { "Linux", "16" })
private String systemConfigurationMessage;
```

##Custom property resolution

The extension already provides a default property resolver so the properties resolution is done **transparently** for the application.

If a custom property resolution strategy is needed one may define a custom property resolver method:
```java
@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, 
  @PropertyBundle String bundleName, String key){
  
  String result = // resolve the property;
  return result;
  
}
```

One needs only to define a method annotated with `@PropertyResolver` and the extension will use this method as the property resolver, overriding the default one. **Note**: In the scenario where a custom resolver method is provided by the application, the method must be defined in a CDI managed bean.

As we have seen above, the property resolver method will be provided with the necessary contextual metadata for property resolution. Additionally, one may also define the method with extra parameters: The CDI container will responsible for all parameter injection. The following definition is also valid:
```java
@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, 
  @PropertyBundle String bundleName, String key, 
  SomeManagedBean someManagedBean, OtherManagedBean otherManagedBean){
  
  // use injected someManagedBean and otherManagedBean instances
  
  String result = // resolve the property;
  return result;
  
}
```

In this example, the CDI container will fetch `SomeManagedBean` and `OtherManagedBean` instances and inject them into the method, always respecting the CDI scopes defined in the injected beans.

It is also possible to define the method without providing all the metadata parameters. The following resolver method definitions are also valid:
```java
@PropertyResolver
public String resolveProperty(String key){
  String result = // resolve the property;
  return result;
}

@PropertyResolver
public String resolveProperty(@PropertyBundle String bundleName, String key){
  String result = // resolve the property;
  return result;
}

@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, String key){
  String result = // resolve the property;
  return result;
}
```

The bundle name and the locale parameters are optional. If present, they must be annotated with `@PropertyBundle` and `@PropertyLocale` annotations respectively.

The only restriction resides in the metadata parameters order. If present, they **must** be defined in the following order: 

1. Locale (optional)
2. Bundle name (optional)
3. Key (mandatory)

The order of any other existing extra parameters is irrelevant (as we have just seen above, it is possible to add extra parameters to the resolver method: they will be injected by the CDI container).

Since the application is free to override the default property resolver method, one may easily define a custom method that fetches properties from an arbitrary external system or database:

```java
@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, 
  String key, ResourceBundleDao resourceBundleDao){
  
  String result = // resolve the property from an external 
                  // data source using resourceBundleDao, locale, and key;
  return result;
  
}
```

##Configuration

The extension is ready to use just by making the compiled `jar` file available in the application classpath.

However, there is a scenario where a little configuration step is needed. As we have seen in the **Usage** section, we are able to inject properties without defining the resource bundle name.

In this case, a default resource bundle will be used. The default resource bundle name may be defined in the following couple of ways, depending on the application context:

- Web application
  * The default resource bundle name is defined as a context parameter named `defaultResourceBundleBaseName` in `web.xml`:
    
  ```xml
  <context-param>
    <description>Default resource bundle name</description>
    <param-name>defaultResourceBundleBaseName</param-name>
    <param-value>com.example.messages</param-value>
  </context-param>
  ```
  
  This configuration assumes that the default message bundle is the `/com/example/messages.properties` file. Of course there may also exist `messages.properties` files for other locales than the default (ex: `messages_fr.properties`). The extension will use the appropriate file depending on the contextual locale during property resolution.

- EJB application
  * The default resource bundle name is defined in the extension's own properties file. The file **must** be named `CDIProperties.properties` and be available at the classpath root. The property file must contain a property named `defaultResourceBundleBaseName` which defines the default resource bundle name:
  
  ```java
  defaultResourceBundleBaseName = com.example.messages
  ```
  
  This configuration is equivalent to the one we have just shown for the Web application context, so it also assumes that the default message bundle is the `/com/example/messages.properties` file.
  
##Additional considerations

If the extension is used in the stand-alone EJB application configuration, and **if we define a custom property resolver method with extra parameters to be injected by the CDI container**, the injected parameter classes must be available to the resolver method classloader.

While the previous assumption may seem obvious, it should be noted that in Java EE applications, especially those bundled in an EAR file, every module will have its own classloader. This means that if the CDI Properties extension `jar` file is placed in the EAR library directory, it will not be able to inject dependencies that are deployed, for example, in a EJB module. It will only be able to inject dependencies that are available in the EAR library directory.

One possible workaround is to deploy the CDI Properties extension in a decompressed format, with its classes extracted inside the EJB module itself. Other workaround may consist in fetching the EJBs within the resolver method through JNDI lookups. This way we may fetch EJBs that are deployed inside EJB modules that are unreachable from the EAR library directory.

The previous restrictions **do not apply** to web applications (WAR files). If the extension is used in a web application context, and the extension `jar` file is deployed in the `WEB-INF/lib` directory, if we define a custom property resolver method with extra injectable parameters, the parameter classes may be located anywhere within the WAR file (`WEB-INF/lib` or `WEB-INF/classes` directory or both).

##Integration Tests

CDI Properties includes Arquillian based Integration Tests (resorting to Arquillian Drone in order to automate front-end application testing) that cover the following use case scenarios:

 - **JSF web based application (Extension's default property resolver method)**

   - Single war module
   - CDI bean supports two identical views, each one with its own `Locale`
   - The extension injects properties into the CDI bean fields, which are presented in the views
   - The extension's own default property resolver method is used

 - **JSF web based application (Application provided custom property resolver method)**

   - Single war module
   - CDI bean supports two identical views, each one with its own `Locale`
   - The extension injects properties into the CDI bean fields, which are presented in the views
   - The application provides a custom property resolver method
   - The resolver method contains additional parameters that are injected by the container. The parameters are CDI managed beans, each one with a distinct CDI bean scope
   - An EJB is also injected as a parameter into the property resolver method. During property value resolution, the Integration Test persists (and reads) a testing entity to the underlying data store
 
 - **EJB Module (Extension's default property resolver method)**

   - EAR containing an EJB module is deployed to the container
   - The extension's own default property resolver method is used
   - Another application (web application) is deployed into the container
   - The web application invokes a Remote EJB from the EJB module
   - The extension injects properties into the fields of the Remote EJB which are presented in the web application
   - The EJB persists (and reads) a test entity to the underlying data store before returning the information to the web application

 - **EJB Module (Application provided custom property resolver method)**

   - EAR containing an EJB module and a library JAR - containing the provided custom resolver method - is deployed to the container
   - Another application (web application) is deployed into the container
   - The web application invokes a Remote EJB from the EJB module
   - The extension injects properties into the fields of the Remote EJB which are presented in the web application
   - The provided property resolver method contains additional parameters that are injected by the container. The parameters are CDI managed beans
   - While resolving the properties, the resolver method fetches another EJB from the EJB module via JNDI and invokes methods that persist and read data from an underlying data store
