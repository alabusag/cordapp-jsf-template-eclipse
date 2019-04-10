

# CorDapp JSF Template Eclipse

This is a CorDapp template for eclipse with integrated JSF webserver.

We created this Template, because we wanted to use  JSF-Technology together with Corda. This template is free to use for everyone. Feel free to propose improvements, issues or other things.

The resources we used to create this template were:

     https://docs.corda.net/tutorial-cordapp.html
     https://github.com/balajimore/cordapp-template-java-eclipse 
     https://auth0.com/blog/developing-jsf-applications-with-spring-boot/

Special thanks to these authors which provided the knownledge for us to create this template.

# Pre-Requisites


You will need the following installed on your machine before you can start:

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
  installed and available on your path (Minimum version: 1.8_131).
* [Eclipse](http://www.eclipse.org/downloads/) (Minimum version MARS.2)
* [Eclipse Buildship: Eclipse Plug-ins for Gradle](https://projects.eclipse.org/projects/tools.buildship/downloads)
* git
* Optional: [h2 web console](http://www.h2database.com/html/download.html)
  (download the "platform-independent zip")

For more detailed information, see the
[getting set up](https://docs.corda.net/getting-set-up.html) page on the
Corda docsite.


## Getting Set Up

To get started, clone this repository with:

     git clone https://github.com/alabusag/cordapp-jsf-template-eclipse

And change directories to the newly cloned repo:

     cd cordapp-jsf-template-eclipse

Refer to the corda docs to start the application:

      https://docs.corda.net/tutorial-cordapp.html

## Configure JSF on the CordApp

Switch to the 'Client' Sub-Project.

Set the Context-Root for your Application in file:

      clients\src\main\resources\application.properties

Set the faces context in file:

      clients\src\main\webapp\WEB-INF\web.xml

And in SpringBootApplication Java File      

      clients\src\main\java\com\alabus\cordapp\template\client\webserver\Starter.java
      
Normally you won't need the web.xml file when starting a JSF-Application with Spring Boot. But I have not found a solution yet. It's not working without the web.xml file.

## Running the webserver on the nodes

Launch the Gradle Tasks:

      runPartyAWebServer
      runPartyBWebServer
      runPartyCWebServer

You can now connect to them with:

      Party A: http://localhost:10097/<contextRoot>/node/iou-form.xhtml
      Party B: http://localhost:10098/<contextRoot>/node/iou-form.xhtml
      Party C: http://localhost:10099/<contextRoot>/node/iou-form.xhtml

# Extending the template

You should extend this template as follows:

* Add your own state and contract definitions under `contracts/src/main/java/`
* Add your own flow definitions under `workflows/src/main/java/`
* Extend or replace the client and webserver under `clients/src/main/java/`

For a guided example of how to extend this template, see the Hello, World! tutorial 
[here](https://docs.corda.net/hello-world-introduction.html).
