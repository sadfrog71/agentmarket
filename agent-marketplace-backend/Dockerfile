FROM maven:3.9.11-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -DskipTests clean package

FROM eclipse-temurin:17-jre-jammy
ENV TZ=Asia/Shanghai
WORKDIR /app
RUN mkdir -p /opt/ruoyi/upload
COPY --from=builder /workspace/ruoyi-admin/target/ruoyi-admin.jar /app/ruoyi-admin.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","exec java ${RUOYI_JAVA_OPTS:--Xms128m -Xmx384m -XX:MaxMetaspaceSize=192m -XX:MaxDirectMemorySize=64m} -jar /app/ruoyi-admin.jar"]
