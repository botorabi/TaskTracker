FROM openjdk:11
COPY tasktracker/ /app
CMD cd /app; java -jar bin/tasktracker.jar
