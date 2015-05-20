FROM java:8-jre

ADD ${project.build.finalName}.jar excel2test.jar

EXPOSE 4567

ENTRYPOINT ["java", "-jar", "excel2test.jar"]