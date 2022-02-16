# read-large-logs
Reads a large logs files and does operations on it

## Steps to run locally
1. Clone the project into your local
2. Run "mvn clean install" command

### Option 1
1. Create a Run Configuration with main class as Application.java
2. Provide complete path of the file to be read as the first argument in the Program arguments

### Option 2 (Assumes you have Java in your PATH)
1. Locate read.large.log.files-1.0-SNAPSHOT.jar in the target folder
2. Open command prompt, navigate to the directory where above jar is located and run "java -jar  read.large.log.files-1.0-SNAPSHOT.jar <Path_of_log_file>
    e.g. java -jar .\read.large.log.files-1.0-SNAPSHOT.jar D:\output.txt
   

## Important Information
1. When application runs, it creates in memory hsql db with url "jdbc:hsqldb:mem:testdb;DB_CLOSE_DELAY=-1" and username sa
2. Application also prints which event ids took more than 4 milliseconds with info severity
3. Application before finishing prints how many total events are processed and how many needs to be alerted.
