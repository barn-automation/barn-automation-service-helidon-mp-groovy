FROM adoptopenjdk/openjdk8:jdk8u202-b08-slim
#FROM openjdk:8-jre-slim
#FROM openjdk:8-jre-alpine
#FROM resin/raspberry-pi-alpine-openjdk:openjdk-8-jre

RUN mkdir /app && mkdir /wallet
COPY ${project.artifactId}.jar /app
COPY .oci /.oci
COPY libs /app
COPY wallet /wallet

CMD ["java", "-jar", "-Doracle.net.tns_admin=/wallet", "-DoutgoingStreamId=", "-DincomingStreamId=ocid1.stream.oc1.phx.aaaaaaaa43rirudiobxctznkkfds4jhhnejnqzhwq5754fejy6o4gc7tkcya", "-DociConfigPath=/.oci/config", "-Dapp.oracle.url=jdbc:oracle:thin:@barnevents_low?TNS_ADMIN=/wallet", "-Djavax.net.ssl.trustStore=/wallet/truststore.jks", "-Djavax.net.ssl.keyStore=/wallet/keystore.jks", "-Doracle.net.ssl_server_dn_match=true", "-Doracle.net.ssl_version=1.2", "/app/${project.artifactId}.jar"]
