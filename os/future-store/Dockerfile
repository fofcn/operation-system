FROM moxm/java:1.8-full
ARG JAR_FILE=target/trickyfs.jar
LABEL maintainer="errorfatal89@gmail.com"
ENV TZ=Asia/Shanghai JAVA_OPTS="-Xms2048m -Xmx2048m -Djava.security.egd=file:/dev/./urandom"
WORKDIR trickyfs/store

EXPOSE 60000
EXPOSE 60001

CMD sleep 10; java $JAVA_OPTS -jar $JAR_FILE


