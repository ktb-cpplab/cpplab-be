# Java 21 베이스 이미지로 시작하여 Gradle 설치
FROM openjdk:21-jdk-slim AS builder

RUN apt-get update 
RUN apt-get install -y curl unzip

# 소스 복사 및 작업 디렉토리 설정
WORKDIR /app
COPY . /app

RUN chmod +x ./gradlew
# 빌드 방식 다르다면 바꾸기
RUN ./gradlew clean build -x test

# 실행을 위한 새로운 Java 21 베이스 이미지
FROM openjdk:21-jdk-slim

# 빌드된 JAR 파일 복사
WORKDIR /app
COPY --from=builder /app/build/libs/cpplab-0.0.1-SNAPSHOT.jar /app/app.jar

# application.properties 파일을 config 폴더에 복사
COPY application.yml /app/config/application.yml
COPY prometheus.yml /app/config/prometheus.yml

# 애플리케이션 포트 노출
EXPOSE 8080

# 애플리케이션 실행
CMD ["java", "-jar", "/app/app.jar"]
