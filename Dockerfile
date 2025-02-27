# 1. 베이스 이미지 설정 (JDK 17 사용)
FROM eclipse-temurin:17-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 파일 복사
ARG JAR_FILE=build/libs/NomNom-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 4. 환경 변수 설정 (docker-compose에서 주입됨)
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENV ADMIN_CODE=${ADMIN_CODE}
ENV GOOGLE_API_KEY=${GOOGLE_API_KEY}

# 5. 컨테이너 실행 시 사용할 명령
ENTRYPOINT ["java", "-jar", "app.jar"]