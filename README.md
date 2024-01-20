# File Parsing Service

### The Project

This is a back-end service that exposes one REST endpoint that allows to parse a file received as input and produce an 
output file using, in the JSON format, containing just the relevant data.

```System.nanoTime()``` is used as soon as an HTTP request is received and right before its response is returned to 
calculate the response time of the web service.

An HTTP request is blocked if the IP address is from one of the specified countries or ISPs/Data centers.  
In order to retrieve data about the IP address, an external service is used, *ip-api.com*.  
If the HTTP request is blocked, a ```403 Forbidden``` HTTP status code is returned, alongside with a specific error message that explains why it was blocked.

Some attention has been paid to validating the file and its data. A check is carried out on the number of columns, 
if the file contains empty lines or the special characters "/n", they are removed because they are not needed, however the strings 
can take on any content, except for the fullName fields that must consist of at least two words.  
In the case in which the file contains non-valid data, a custom ```MalformedFileException``` will be thrown and a 
```400 Bad Request``` HTTP status code will be returned.

Audit data are store in an H2 Database each time an HTTP request is received.

### Technologies
This is a **Java 21** project, which uses **Maven**.  
**Spring Boot** was used to expose the REST service, make REST api calls to the external service, save data to database,
and test the exposed endpoint.  
**Lombok** library that provides getters, setters, constructors, etc. to avoid boilerplate code when writing model classes.  
The **json-simple** library was used to write and manage JSON objects and arrays.  
Both *Unit* and *Integration tests* were written using **JUnit** to create test cases, **Mockito** to mock data, 
and **WireMock** to mock the response from REST api calls.
