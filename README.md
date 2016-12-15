creditcard-ms
=============
The micro service has been constructed using the following main technologies:  
- Java 8, Spring Boot  
- Gradle  
- MongoDB  
  
## Build
```
$ cd creditcard-ms/  
```  

Install gradle wrapper  
```
$ gradle wrapper
```  

Running the micro service  
```
$ ./gradlew clean bootRun
```  
  
## MongoDB with docker
````
$ docker run -p 27017:27017 --name mongod -d mongo[ --noprealloc][ --smallfiles][ --nojournal]
```  
