@ECHO OFF

SET JINI_DIR=%~dp0
SET JINI_JAR=${project.artifactId}-${project.version}.jar
SET JINI_PATH="%JINI_DIR%%JINI_JAR%"

java -jar %JINI_PATH% %*