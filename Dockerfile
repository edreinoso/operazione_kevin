# Dockerfile

FROM openjdk:11
RUN mkdir -p ./kv-server
COPY ./ /kv-server
WORKDIR /kv-server
RUN apt-get update && \ 
    apt-get install -y wget unzip
RUN wget https://services.gradle.org/distributions/gradle-5.6.1-bin.zip
RUN mkdir /opt/gradle
# may have to double check the vevrsion of this
RUN unzip -d /opt/gradle gradle-5.6.1-bin.zip
ENV PATH="/opt/gradle/gradle-3.4.1/bin:${PATH}"
RUN ./gradlew installDist
EXPOSE 9100 9101 9102
# finishing up quite quickly
CMD ["sh", "start.sh"]