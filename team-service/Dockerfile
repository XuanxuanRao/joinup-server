FROM azul/zulu-openjdk-alpine:11

WORKDIR /app

# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN mkdir /app/dump
RUN chown -R 1000:1000 /app
RUN chmod -R 777 /app

USER 1000

ENV JAVA_OPTS="\
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/dump \
    -XX:+PrintFlagsFinal \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:MinRAMPercentage=20.0 \
    -XX:+UseStringDeduplication"

# 拷贝jar包
COPY target/*.jar app.jar

# 设置配置文件
ARG PROFILE=prod
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar app.jar"]
