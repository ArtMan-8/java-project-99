FROM gradle:8.12.1-jdk23-corretto

WORKDIR /
COPY / .
RUN ./gradlew installDist

ENV JAVA_OPTS="-Xmx512M -Xms512M"
EXPOSE 8080

CMD ["./build/install/app/bin/app"]
