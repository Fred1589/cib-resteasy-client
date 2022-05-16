
# Microservice project

This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Prerequisites

### Maven 3.8.3 (at least)
https://downloads.apache.org/maven/maven-3/3.8.3/

To verify your installation, run:
```shell script
$ mvn --version
```

### JDK11
https://www.azul.com/downloads/zulu-community/?version=java-11-lts&package=jdk

To verify your installation, run:
```shell script
$ javac -version
```
You should see somethink like:
```shell script
$ javac 11.0.10
```  

### PRINT (CIB)
Print (CIB) instance is needed and the example rtf hast to be uploaded there. See first step in the section Running the 
application


## Running the application in dev mode
###Example RTF
The print service needs an example rtf file uploaded. There can be found one in the rtf directory for this
application. This one has to be uploaded on the print service instance and the template GUID has to be configured
also in the application.properties

### Configuration:
To configure specific things, like file logging or corporate proxy Quarkus offers the application.properties which
can be found under src/main/resources. There is also the URL for the print instance to reach configured. You can
find it with the key quarkus.rest-client.print-api.url

### Start Quarkus:
Run: 
```shell script
$ mvn quarkus:dev
```
**Windows:** http://localhost:8080  
**Linux:** http://localhost:8080

### Service:
The application provides a print service which can be reached with curl:
curl -v -X POST http://localhost:8080/print --output generated.pdf


## Troubleshoot
**Error when executing tests:**  
**Problem:** Intellij Idea 2020 operation problem: Command line is too long. Shorten command line ..  
**Solution:** Modify .idea\workspace.xml under the project, find the tag `<PropertiesComponent>` , and add a line to the tag  
```xml
<property name="dynamic.classpath" value="true" />
```
<br/>

