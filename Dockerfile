FROM openjdk:8

# 设置时区环境变量
ENV TZ=Asia/Shanghai

WORKDIR /app

COPY system/start/target/datactl-1.0.0-SNAPSHOT.jar /app/

#ENV PORT 5000
#EXPOSE $PORT

CMD ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "/app/datactl-1.0.0-SNAPSHOT.jar"]
#CMD [ "sh", "-c", "mvn -Dserver.port=${PORT} spring-boot:run" ]
