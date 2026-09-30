FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/install/backend/ /app/
RUN chmod -R a=rX /app
ENV PORT=8080
EXPOSE 8080
USER 10001:10001
ENTRYPOINT ["/app/bin/backend"]
