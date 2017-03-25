FROM fedora:rawhide
RUN dnf install -y tesseract java-1.8.0-openjdk
ADD solvo_services-0.0.1-SNAPSHOT.jar /app/target.jar
#ADD target.jar /app/
CMD ["java","-jar","/app/target.jar"]
