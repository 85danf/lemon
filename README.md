# Lemon
This application is (hopefully) built according to the provided spec with a few minor additions, it is a simple `Spring Boot` application running an on embedded `Tomcat` backed by an `Apache Derby` database.
Other tools leveraged here are `Spring JPA` and `Hibernate` for db actions, `Spring MVC` for REST mapping and `Swagger` for API documentation.

This application has been setup to use a `Derby` in-memory database
for simplicity, it could be easily configured to use any other database.

 
## Requirements:
min. `java 8` (`brew cask install java8 / java11` or equivalent)

`Maven` (`brew install maven`)



## Usage:
`mvn spring-boot:run` to run locally. The application starts on port `8086` by default (may be changed in the `application.yaml` file)

To run a docker container of this app you can execute the maven target `mvn install docker:build` (Docker deamon must be up),
Doing so will build a docker container locally named `org.danf/lemon`.

I have also made a  container of this version available online so you can simply execute the following to run the app right now:
`docker run -p8086:8086 --name lemon elhefe-lemon.jfrog.io/lemon` 


## Documentation
Rationalization for choices I made and other in-depth explanations can be found as javadoc comments in the code.

All available endpoints of this application are documented under
`http://localhost:8086/lemon/swagger-ui.html`
