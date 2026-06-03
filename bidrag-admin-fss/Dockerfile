FROM gcr.io/distroless/java21
LABEL maintainer="Team Bidrag" \
      email="bidrag@nav.no"

COPY --from=busybox:1.35.0-glibc /bin/sh /bin/sh
COPY --from=busybox:1.35.0-glibc /bin/printenv /bin/printenv

WORKDIR /app

COPY ./target/app.jar app.jar

EXPOSE 8080
ENV LANG=nb_NO.UTF-8 LANGUAGE='nb_NO:nb' LC_ALL=nb_NO.UTF-8 TZ="Europe/Oslo"
ENV SPRING_PROFILES_ACTIVE=nais

ENTRYPOINT ["/bin/sh", "-c", "exec java ${JAVA_PROXY_OPTIONS:-$JDK_JAVA_OPTIONS} -Dloader.path=/var/run/secrets/db2license -cp app.jar org.springframework.boot.loader.launch.PropertiesLauncher"]