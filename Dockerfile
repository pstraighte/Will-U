FROM azul/zulu-openjdk:17

# 독커 작업 디렉토리 지정
WORKDIR /app

# jar 파일을 파일 경로로 복사
COPY build/libs/Will-U-0.0.1-SNAPSHOT.jar Will-U-0.0.1-SNAPSHOT.jar

# 통신에 사용할 포트 노출
EXPOSE 8080

# 컨테이너 실행 시 사용할 명령어
ENTRYPOINT ["java", "-jar", "Will-U-0.0.1-SNAPSHOT.jar"]
