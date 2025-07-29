# ExtendedManager

### 📑 Table of Contents
- [Backend Design](#backend-design)
  - [Entity / DTOs / Mapper / Repo](#entity--dtos--mapper--repo)
  - [Config / Security / Exception](#config--security--exception)
  - [Service / Implementation / Controller / APIs](#service--implementation--controller--apis)
- [Testing and Coverage](#testing-and-coverage)
- [Branch Management](#branch-management)
- [Setup Guide](#setup-guide)
  - [Environment Setup](#environment-setup)
  - [Eclipse Backend Setup](#eclipse-backend-setup)
  - [Eclipse AWS Setup](#eclipse-aws-setup)
  - [Eclipse Frontend Setup](#eclipse-frontend-setup)
- [Contributors](#contributors)

# Backend Design
- Note: Files for designs can be found under the Diagram folder.

## Entity / DTOs / Mapper / Repo
![Image_Failed](Diagrams/Entity_DTO_Mapper_Repo.png)
![Image_Failed](Diagrams/SQL_Tables.png)

## Config / Security / Exception
![Image_Failed](Diagrams/Config_Security_Exception.png)

## Service / Implementation / Controller / APIs
![Image_Failed](Diagrams/Service_Impl_Controller.png)

# Testing and Coverage

### WolfCafe Backend Statement / Branch Coverage

![Backend Coverage](.github/badges/jacoco-backend.svg)
![Backend Branch Coverage](.github/badges/jacoco-branches.svg)

### WolfCafe Frontend Statement/Branch Coverage

![Frontend Coverage](.github/badges/coverage-frontend.svg)
![Frontend Branch Coverage](.github/badges/frontend-branches.svg)

### Run Frontend Test
- remove wolf-cafe-frontend/node_modules and wolf-cafe-frontend/package-lock.jspn.
- In wolf-cafe-frontend, run npm install --save-dev vitest
- run npm install --save-dev @testing-library/react @testing-library/jest-dom @testing-library/user-event
- run npm run test to start tests.
- if you want to run coverage run npm run test:coverage
  
### Run Backend Test
You can run all of the tests at once by right clicking on the src/test/java folder and selecting Run As > JUnit.

### Run Maven Test
Right click on pom.xml and select Run As > Maven Test

# Branch Management
- Features should have their own unique branch off of the development branch
- Each feature branch should be split into two sub-branches for frontend (FE) and backend (BE)
- Ex: If you implement a Stock feature, the branching would be:
  - main <- development <- stocks <- stocks_BE
  - main <- development <- stocks <- stocks_FE

# Setup Guide

## Environment Setup
1. Install jdk-17 and Apache-Maven (make sure to test in terminal that they are local)
2. Add Maven certificate to your device
  - Go to https://repo.maven.apache.org and click the lock icon in URL bar
  - Select Certificate (Valid) and the certificate will pop up
  - Go to the details tab and click export in lower right corner
  - Dwnload file in Base-64 encoded and save it where it can be globally accessed
  - Add it to your jdk path using the command below: [note it uses my file locations, change it to your own]
    "C:\Program Files\Java\jdk-21\bin\keytool.exe" -import -trustcacerts -alias mavenRepo -file "C:\Users\[your username]\repo.maven.apache.org.crt" -cacerts -storepass changeit
  - Clean maven cache using: mvn dependency:purge-local-repository
  - Run maven build again: mvn clean install

## Eclipse Backend Setup
Import project into Eclipse
1. Open Eclipse Switch to the Java Perspective (upper right corner).
2. Open Git Repositories view: Window > Show View > Other > Git > Git Repositories
3. Clone the repo: Clone a Git Repo -> add clone URI and add your github credentials -> Click Next -> Click Next -> Optional: change local directory -> Click Finish
4. Import the Maven Project
 - In Package Explorer select Import Projects -> Maven -> Existing Maven Projects
 - Browse for the repository directory. The selected root directory should be local_path\ExtendedManager
 - Click Add project(s) to working set and click Finish
 - The project should be in the Package Explorer
5. Install Lombok
Lombok is a library that lets us use annotations to automatically generate getters, setters, and constructors.  For Lombok to work in Eclipse (and other IDEs like IntelliJ or VS Code), you need to set up Lombok with the IDE in addition to including in the pom.xml file.

Follow the [instructions for setting up Lombok in Eclipse](https://projectlombok.org/setup/eclipse).  Make sure you download the laste version of Lombok from [Maven Repository](https://mvnrepository.com/artifact/org.projectlombok/lombok) as a jar file.

6. Install Node.js
7.  Set up SQL: Make sure your SQL database is set up and running
  * In src/main/resource, make a copy of application.properties.template named application.properties [NOTE: the .gitignore stops it from being pushed to github for saftey measures] and in the files do the following:
  * Set `spring.datasource.password` to your local MySQL password`
  * Set `app.jwt-secret` by encrypting any phrase using SHA256 into app.jwt-secret, tool I used to generate it was https://emn178.github.io/online-tools/sha256.html
  * Set `app.admin-user-password` to a plain text string that you will use as the admin password.
8. Rightclick the pom.xml file and select Maven > Update Project then click Ok.
9. Run the JUnit test by right-clicking src/test/java > Run as > Junit Test, it should pass ALL test except AWS test at this point
10. Run the project by right clicking on the project application (WolfCafeApplication.java) and selecting Run As > Java Application. 
    
## Eclipse AWS Setup
[Note: AWS sometimes automatically changes your region, if this occurs then change it back to the prior reigion in env variables, or update env variable to new region]
1. Create a AWS Account and generate a AWS Secret Access Key
2. Store the key locally in environment variables
   - Linux/macOS: Type the following commands with key values
       * export AWS_ACCESS_KEY_ID=your_access_key_here
       * export AWS_SECRET_ACCESS_KEY=your_secret_key_here
       * export AWS_REGION=your_region
       * source ~/.bashrc
   - Windows:
      1. Open System Properties → Advanced → Environment Variables
      2. Under "User variables" or "System variables", click New.
      3. Add:
        * Name: AWS_ACCESS_KEY_ID, Value: your key
        * Name: AWS_SECRET_ACCESS_KEY, Value: your secret
        * Name: AWS_REGION, Value: your region 
4.Now we will setup Cloudwatch in AWS to monitor the logs, to do this search for Cloudwatch in services and under Logs create a log group named SpringBootLogs with a stream named LocalStream. Note it is vital the naming convention matches the CloudWatcherAppender. 
5. Run the JUnit test by right-clicking src/test/java > Run as > Junit Test, it should pass all test since you should have all backend components set up by now. The important ones to note for this portion is that AwsCredentialsIntegrationTest passes (means you set up env variables right and can connect to AWS) and LogsTest pass. NOTE: LogsTest reqyures you to check both that it is able to send the logs to AWS and also within the cloudwatch feature to make sure it gets it, a wrong setup scheme can have the test pass, but it not reach the correct destination. 

## Eclipse Frontend Setup
1. Verify the backend is running (step 9 of backend setup)
2. In the Git Repositories View, right click wolf-cafe-frontend under the working tree folder and select Import Projects > Finish
3. Open Terminal View (Window > Show View > Terminal) and cd into the wolf-cafe-frontend directory (should be in something like ...\ExtendedManager\wolf-cafe-frontend>
4. Create the node_modules directory by typing the following in the terminal: % npm install
7. Run the frontend by typing into the terminal: %npm run dev
8. View in browser by typing: http://localhost:3000

# Contributors
- The project is currently being ran by Brandon Wroblewski (BrandonWrob) and Andrew Anufryienak
- The project is an extension of Store-Management-Application which was developed by Brandon Wroblewski (bnwroble), wtwalton, sadusum3, olweaver, and sesmith5.
