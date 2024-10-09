FROM openjdk:21
COPY target/*.jar chroniclequests.jar
ENTRYPOINT ["java", "-jar", "/chroniclequests.jar"]