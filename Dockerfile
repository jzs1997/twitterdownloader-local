FROM ubuntu:22.04

RUN apt-get update -y && \
    apt-get install -y python3.10 && \
    apt-get install -y python3-pip &&\
    pip install opencv-python && \
    pip install numpy && \
    pip install imageio && \
    pip install pygifsicle && \
    apt-get install -y openjdk-17-jdk openjdk-17-jre

EXPOSE 8091

ADD target/twitterdownloader-0.0.1-SNAPSHOT.jar app.jar
ADD src/main/java/com/example/twitterdownloader/externals/video_converter.py video_converter.py

ENTRYPOINT ["java", "-jar", "app.jar"]
