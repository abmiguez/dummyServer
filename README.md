![picture alt](https://img.shields.io/badge/Java-1.8-blue.svg "Minimum Java version")
![picture alt](https://img.shields.io/badge/build-passing-brightgreen.svg "Build passing")
[![picture alt](https://img.shields.io/badge/license-unlicense-informational.svg "Unlicense")](https://github.com/abmiguez/dummyServer/blob/master/LICENSE.md)

## Dummy Server in Java ##
The code provided in this Github page is an Annotation Server prototype built in Java language. 

The Dummy Server is designed to help researchers and developers to build a simply system in order to listen and respond 
BeCalm requests as quickly as possible, during the TIPS task in the BioCreative V.5 competition. This example is done using only Java Sockets, so 
it is recommended the implementation of a more complete and specific solution like 
[HTTPServer](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/package-summary.html "HTTPServer"), 
[NanoHTTPD](https://github.com/NanoHttpd/nanohttpd "NanoHTTPD") or 
[Jetty](https://www.eclipse.org/jetty/ "Jetty").

### Glossary ###
* [BioCreative V.5 competition](https://biocreative.bioinformatics.udel.edu/resources/publications/bcv5_proceedings "BioCreative V.5 competition")
* [TIPS task and Annotation Servers](https://biocreative.bioinformatics.udel.edu/media/store/files/2017/BioCreative_V5_paper3.pdf "TIPS task and Annotation Servers")
* [BeCalm Metaserver](http://www.becalm.eu/ "BeCalm metaserver")
 
### Quick install ###
Clone the repository and make a JAR (select the main class Server) or simply download the most recent release. Then, open a terminal and execute the following command to start the Server:

`java [-Xmx2G] [-Xms256M] -server -jar dummyserver.jar`

Note that, the parameters between brackets are optional. However, it may be interesting to modify the maximum memory allocation and the initial memory allocation pool when starting the JVM (`-Xmx` and `-Xms`, respectively).

Once the Annotation Server is running, it will be possible to make requests! At this point, you have two options to test the dummy server:

1. If you clone the code and open it in a IDE, you will see a JUnit test to make this requests automatically. To do this, you must execute the Server class first. This JUnit test will send random requests to test the `getState` and `getAnnotations` methods.
2. You can send POST requests to `127.0.0.1:8088` to test the `getState` method. The requests must be in JSON format and it is mandatory to use the correct `becalm_key`. See the following example:
`curl -H "Content-Type: application/json" -X POST http://127.0.0.1:8088 -d "{\"name\": \"BeCalm\",\"method\": \"getState\",\"becalm_key\": \"b907e0df6bbc124844ae97dea98d3d0fc059c133\",\"custom_parameters\": {\"example\": true},\"parameters\": {}}"`

### License ###
Copyright © 2019, Aitor Blanco Míguez. Released under the Unlicense license.