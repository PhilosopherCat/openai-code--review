FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制 Maven 配置和源码
COPY pom.xml .
COPY openai-code-review-sdk/pom.xml openai-code-review-sdk/
COPY openai-code-review-test/pom.xml openai-code-review-test/

# 下载依赖（利用缓存）
RUN mvn dependency:go-offline -pl openai-code-review-test -am

# 复制源码
COPY openai-code-review-sdk/ openai-code-review-sdk/
COPY openai-code-review-test/ openai-code-review-test/

# 构建
RUN mvn package -pl openai-code-review-test -am -DskipTests

# 运行阶段
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/openai-code-review-test/target/*.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
