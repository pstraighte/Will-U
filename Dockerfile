FROM openjdk:17-jdk
#독커 작업 디렉토리 지정
WORKDIR /app
#jar를 파일 경로 입력 , app.jar를 복사된 파일명
COPY build/libs/Will-U-0.0.1-SNAPSHOT.jar app.jar
#통신 사용 할 포트
EXPOSE 8080
#컨테이너 실행때 쓸 명령어들
CMD ["java", "-jar", "app.jar"]