FROM openjdk:11
VOLUME /tmp
EXPOSE 8080
ADD ./target/jbcatalog-0.0.1-SNAPSHOT.jar jbcatalog.jar
ENTRYPOINT ["java", "-jar", "/jbcatalog.jar"]