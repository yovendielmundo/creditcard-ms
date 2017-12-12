creditcard-ms
=============
The micro service has been constructed using the following main technologies:  
- Java 8, Spring Boot  
- Gradle  
- MongoDB  
  
## Build

1. Install gradle wrapper  
    ```
    $ gradle wrapper
    ```  

2. Make sure you have MongoDB server running  and build the micro service  
    ```
    $ ./gradlew clean build
    ```  

3. Running the micro service  
    ```
    $ ./gradlew bootRun
    ```  
  
## MongoDB with docker

````
$ docker run -p 27017:27017 --name mongod -d mongo[ --noprealloc][ --smallfiles][ --nojournal]
```  
