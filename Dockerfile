# Java 21 베이스 이미지로 시작하여 Gradle 설치
FROM openjdk:21-jdk-slim AS builder

# Gradle 설치
RUN apt-get update && \
    apt-get install -y curl unzip && \
    curl -sSL https://downloads.gradle-dn.com/distributions/gradle-7.5-bin.zip -o gradle.zip && \
    unzip gradle.zip -d /opt && \
    rm gradle.zip && \
    ln -s /opt/gradle-7.5/bin/gradle /usr/bin/gradle

# 소스 복사 및 작업 디렉토리 설정
WORKDIR /app
COPY . /app

# Gradle 빌드 실행 (테스트 제외)
RUN gradle build -x test

# 실행을 위한 새로운 Java 21 베이스 이미지
FROM openjdk:21-jdk-slim

# 빌드된 JAR 파일 복사
WORKDIR /app
COPY --from=builder /app/build/libs/cpplab-0.0.1-SNAPSHOT.jar app.jar

# application.properties 파일을 config 폴더에 복사
COPY application.properties /app/config/application.properties

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]
