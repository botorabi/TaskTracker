FROM openjdk:11
COPY ./tmp/tasktracker/ /app
CMD cd /app; java -jar bin/tasktracker.jar
