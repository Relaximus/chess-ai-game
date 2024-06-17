FROM ghcr.io/graalvm/native-image-community:22 as build

COPY . ./app
WORKDIR ./app
RUN echo "$JAVA_HOME"
RUN ./mvnw -v
RUN ./mvnw -Pnative clean native:compile
RUN ls .

FROM ubuntu

COPY --from=build ./app/app/target/check-engine ./check-engine

ENTRYPOINT exec ./check-engine