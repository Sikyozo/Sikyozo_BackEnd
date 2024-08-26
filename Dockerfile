FROM openjdk:17-jdk-slim
LABEL authors="sanghoon"

WORKDIR /app

# 2. 소스 코드를 컨테이너에 복사
COPY . .
COPY .env.dev .
COPY .env.prod .

# 3. Gradle 빌드를 수행
RUN chmod +x ./gradlew

RUN ./gradlew build

ENV SPRING_PROFILES_ACTIVE=prod

# 4. 빌드된 JAR 파일을 실행
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/build/libs/sikyozo.jar"]