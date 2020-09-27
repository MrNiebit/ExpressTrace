FROM java:8
MAINTAINER gitsilence <gitsilence@lacknb.cn>
EXPOSE 8080
ADD *.jar app.jar
ENTRYPOINT ["java", "-Dfile.encoding=utf-8" ,"-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
