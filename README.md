#CDI Properties

**Build + Integration Tests status:**
<br /><a href="https://travis-ci.org/gonmarques/cdi-properties" target="_blank"><img src="https://travis-ci.org/gonmarques/cdi-properties.svg?branch=master" alt="Build Status" /></a>

**What's new (1.1.0/1.1.1):**
- Usage in a web application is not coupled to JSF anymore, so now it may be deployed in any environment
- Applications may optionally provide a custom Locale resolver
- Custom property resolver parameters order is not strict anymore

<strong>Issues and feature requests may be opened <a href="https://github.com/gonmarques/cdi-properties/issues" target="_blank">here</a>.</strong>

**Note**: CDI Properties includes a set of Arquillian based Integration Tests that cover the most common use case scenarios (from the simplest scenario to others that explore some degree of complexity). Detailed information about the included Integration Tests is available in the last section of this README file.

**Maven Central Repository coordinates (latest release):**
```xml
<dependency>
  <groupId>com.byteslounge</groupId>
  <artifactId>cdi-properties</artifactId>
  <version>1.1.1</version>
</dependency>
```

##Introduction

CDI Properties is a CDI extension that enables resource bundle injection in a CDI enabled Java EE application, requiring little to no configuration depending on the application requirements. The resource bundle source may be the typical `.properties` files or any other external resource or system.

The extension may be used in two distinct Java EE scenarios:

- Java web application
- EJB standalone application

The extension takes internationalization into account, meaning that it will resolve the correct resource bundles according to the contextual Locale during resource bundle resolution.

The resolved properties are also converted to the expected type, meaning that if a property is injected into an `Integer` field, the resolved property will be converted to an `Integer` instance.

##Usage

The `@Property` annotation is used to inject property values into components that are managed by the CDI container (this is valid for CDI managed beans and also for EJBs).

Examples:

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

Message formatting is also possible:
```java
system.message = The system is running a {0} box with {1} MB of available RAM

@Property(value = "system.message", parameters = { "Linux", "16" })
private String systemConfigurationMessage;
```

##Property Resolver

The extension already provides a default property resolver so the properties resolution is done **transparently** for the application.

If a custom - or more complex - property resolution strategy is needed, one may define a custom property resolver method:
```java
@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, 
  @PropertyBundle String bundleName, @PropertyKey String key){
  
  String result = // resolve the property;
  return result;
  
}
```

One needs only to define a method annotated with `@PropertyResolver` and the extension will use this method instead as the property resolver, overriding the default one. **Note**: The method itself must be defined in a CDI managed bean.

As we have seen above, the property resolver method will be provided with the necessary contextual metadata for property resolution. Additionally, one may also define the method with extra parameters: The CDI container will be responsible for all parameter injection. The following definition is also valid:
```java
@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, 
  @PropertyBundle String bundleName, @PropertyKey String key, 
  SomeManagedBean someManagedBean, OtherManagedBean otherManagedBean){
  
  // use injected someManagedBean and otherManagedBean instances
  
  String result = // resolve the property;
  return result;
  
}
```

In this example, the CDI container will fetch `SomeManagedBean` and `OtherManagedBean` instances and inject them into the method, always respecting the CDI contextual scopes that were defined in the injected beans (ex: `@RequestScoped`, `@SessionScoped`, etc.).

It is also possible to define the method without providing all the metadata parameters. The following resolver method definitions are also valid:
```java
@PropertyResolver
public String resolveProperty(@PropertyKey String key){
  String result = // resolve the property;
  return result;
}

@PropertyResolver
public String resolveProperty(@PropertyBundle String bundleName, @PropertyKey String key){
  String result = // resolve the property;
  return result;
}

@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, @PropertyKey String key){
  String result = // resolve the property;
  return result;
}
```

The bundle name and the locale parameters are optional. On the other hand, the key parameter is mandatory. Remember that the metadata parameters **must** be annotated with `@PropertyKey`, `@PropertyBundle` or `@PropertyLocale`, if present of course.

The parameters may be defined in **any** given order. This is also valid for parameters that represent beans to be injected into the resolver method by the CDI container. For example, the following definition contains a mixture of randomly ordered parameters and is also **valid**:

```java
@PropertyResolver
public String resolveProperty(@PropertyBundle String bundleName, SomeManagedBean someManagedBean, 
  @PropertyKey String key, OtherManagedBean otherManagedBean,
  @PropertyLocale Locale locale){
  
  String result = // resolve the property;
  return result;
  
}
```

Normally one will define the parameters in a meaningful order, but this was just to demonstrate that one is free to define the parameters in any arbitrary order.

As we have just seen, the application is free to override the default property resolver method, so one may easily define a custom method that fetches properties from an arbitrary external system or database:

```java
@PropertyResolver
public String resolveProperty(@PropertyLocale Locale locale, 
  @PropertyKey String key, ResourceBundleDao resourceBundleDao){
  
  String result = // resolve the property from an external 
                  // data source using resourceBundleDao, locale, and key;
  return result;
  
}
```

##Locale Resolver

The application may also provide a custom locale resolver method which will override the extension's own default locale resolver method. It's similar to what we have seen for the property resolver method but with some changes, but let's see first the extension's default resolver method.

As we have seen earlier, the extension may be used in two distinct scenarios: a web application or a standalone EJB module.

If the extension is used in a web application, and JSF is present in the application's classpath, the **default** locale resolver will try to fetch the current locale from the JSF Faces Context:

```java
FacesContext.getCurrentInstance().getViewRoot().getLocale();
```

If JSF is not present in the application's classpath, the **default** locale resolver will return the system's default locale.

In case the extension is used in a standalone EJB module, the **default** locale resolver will also return the system's default locale.

If a custom locale resolver is required, for example in an web application that is not using JSF, one may define a custom locale resolver method:

```java
@LocaleResolver
public Locale resolveLocale(UserSessionBean userSessionBean){

  return userSessionBean.getLocale();
  
}
```

The custom locale resolver method must be annotated with `@LocaleResolver`. Similarly to what we have seen previously in the property resolver method, one may define the method with an arbitrary set of parameters: they will be injected by the CDI container. This way it becomes easy to fetch the current user locale from any source, like the user session or even an external database or system.

One may even define extention contextual metadata parameters: The container will also inject them. For example, the following definition is also valid:

```java
@LocaleResolver
public Locale resolveLocale(@PropertyBundle String bundleName,
  @PropertyKey String key, UserSessionBean userSessionBean){

  // Use the key or the bundleName for any required reason
  
  return userSessionBean.getLocale();
  
}
```

The only contextual metadata parameter type that is **not** allowed here is the locale (`@PropertyLocale`). Since we are resolving the locale it does not make sense to inject the locale into the method.

##Configuration

The extension is ready to use just by making its own `jar` file available in the application classpath (ex: **WEB-INF/lib** directory if we are running a web application).

However, there is a scenario where a little configuration step is also needed. As we have seen in the **Usage** section, we are able to inject properties without defining the resource bundle name.

In this case, a default resource bundle will be used. The default resource bundle name may be defined in the following ways, depending on the application type:

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

- EJB standalone module
  * The default resource bundle name is defined in the extension's own properties file. The file **must** be named `CDIProperties.properties` and be available at the EJB module classpath root. The property file must contain a property named `defaultResourceBundleBaseName` which defines the default resource bundle name:
  
  ```java
  defaultResourceBundleBaseName = com.example.messages
  ```
  
  This configuration is equivalent to the one we have just shown for the Web application context, so it also assumes that the default message bundle is the `/com/example/messages.properties` file.
  
##Additional considerations

If the extension is used in a standalone EJB module, and **if we define a custom property resolver method with extra parameters to be injected by the CDI container**, the injected parameter classes must be available to the resolver method classloader.

While the previous assumption may seem obvious, it should be noted that in Java EE applications, especially those bundled in an EAR file, every module will have its own classloader. This means that if the CDI Properties extension `jar` file is placed in the EAR library directory, it will not be able to inject dependencies that are deployed, for example, in a EJB module. It will only be able to inject dependencies that are available in the EAR library directory.

If one needs to inject (for example) EJBs into a resolver method that are **not** available in the resolver's own classpath, one possible, but also less desirable, workaround is to deploy the CDI Properties extension in a decompressed format, with its classes extracted inside the EJB module itself. A more acceptable workaround may consist in fetching the EJBs within the resolver method through JNDI lookups. This way we may fetch EJBs that are deployed inside EJB modules that are unreachable from the EAR library directory.

The previous restrictions **do not apply** to web applications (WAR files). If the extension is used in a web application and the extension own `jar` file is deployed in the `WEB-INF/lib` directory as it should, and if we defined a custom property resolver method with extra injectable parameters, the parameter classes may be located anywhere within the WAR file (`WEB-INF/lib` or `WEB-INF/classes` directory or both).

Another important consideration is that `@Stateless` or `@Singleton` EJBs and `@ApplicationScoped` or `@Singleton` CDI managed beans will most likely be shared accross multiple clients, so this should be taken into consideration especially if the current user (or client) locale matters.

##Integration Tests

CDI Properties includes Arquillian based Integration Tests (resorting to Arquillian Drone and Graphene in order to automate front-end application testing) that cover the following use case scenarios:

 - **Web application that is not JSF based using the default resolver method**
 - **JSF based web application using the default property resolver method**
 - **Web application using a custom property resolver method**
 - **Web application with a custom locale resolver method that fetches the current user locale from a CDI session scoped bean**
 - **Web application with a custom locale resolver method that fetches the current user locale from a ThreadLocal variable**
 - **Web application with both custom property and locale resolver methods with CDI managed beans from all scopes being injected into the methods plus EJBs, and the EJBs persist and load data from a database inside the resolver methods**
  
 - **EJB standalone module that uses the default property resolver method**
 - **EJB standalone module with a custom property resolver method with CDI managed beans being injected into the method plus EJBs, and the EJBs persist and load data from a database inside the resolver method**
