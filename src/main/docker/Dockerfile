FROM openjdk:8-jre-slim
#FROM openjdk:8-jre-alpine
#FROM resin/raspberry-pi-alpine-openjdk:openjdk-8-jre

RUN mkdir /app && mkdir /wallet
COPY ${project.artifactId}.jar /app
COPY libs /app
COPY wallet /wallet

CMD ["java", "-jar", "-Doracle.net.tns_admin=/wallet", "-Dapp.oracle.url=jdbc:oracle:thin:@barnevents_low?TNS_ADMIN=/wallet", "-Djavax.net.ssl.trustStore=truststore.jks", "-Djavax.net.ssl.trustStorePassword=${TRUSTSTORE_PASSWORD}", "-Djavax.net.ssl.keyStore=keystore.jks", "-Djavax.net.ssl.keyStorePassword=${KEYSTORE_PASSWORD}", "-Doracle.net.ssl_server_dn_match=true", "-Doracle.net.ssl_version=1.2", "-Dapp.oracle.password=${ORACLE_PASSWORD}", "/app/${project.artifactId}.jar"]
