# Gradle 빌드를 위한 베이스 이미지
FROM gradle:7.5-jdk17 AS builder

# 소스 복사 및 작업 디렉토리 설정
WORKDIR /app
COPY . /app

# Gradle 빌드 실행 (테스트 제외)
RUN gradle build -x test

# 실행을 위한 새로운 베이스 이미지
FROM openjdk:17-jdk-slim

# 빌드된 JAR 파일 복사
WORKDIR /app
COPY --from=builder /app/build/libs/cpplab-0.0.1-SNAPSHOT.jar app.jar

# application.properties 파일을 config 폴더에 복사
# COPY application.properties /app/config/application.properties

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행
CMD ["java", "-jar", "app.jar"]