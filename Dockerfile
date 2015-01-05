FROM docker1.rni.org:5000/centos7-java8

ADD /target/excel2test-1.0.0.jar excel2test.jar

EXPOSE 4567

ENTRYPOINT ["java", "-jar", "excel2test.jar"]