FROM amazoncorretto:17
USER nobody
WORKDIR /application
COPY maven/*.jar executable.jar
ARG PORT=8112
ENV SERVER_PORT=${PORT}
EXPOSE ${SERVER_PORT}
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=15s CMD curl --fail http://localhost:${SERVER_PORT}/actuator/health || exit 1
ENTRYPOINT ["java","-jar","executable.jar"]