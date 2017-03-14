FROM fedora:rawhide
RUN dnf install -y tesseract
RUN dnf install -y java-1.8.0-openjdk
ADD target.jar /app/
CMD ["java","-jar","/app/target.jar"]