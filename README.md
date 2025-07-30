# ExtendedManager

### 📑 Table of Contents
- [Project Overview](#project-overview)
- [Backend Design](#backend-design)
  - [Entity / DTOs / Mapper / Repo](#entity--dtos--mapper--repo)
  - [Config / Security / Exception](#config--security--exception)
  - [Service / Implementation / Controller / APIs](#service--implementation--controller--apis)
- [Testing and Coverage](#testing-and-coverage)
- [Branch Management](#branch-management)
- [Setup Guide](#setup-guide)
  - [Environment Setup](#environment-setup)
  - [Eclipse Backend Setup](#eclipse-backend-setup)
  - [Eclipse Frontend Setup](#eclipse-frontend-setup)
- [Contributors](#contributors)

# Project Overview

A full-stack store management application with database management, automated testing, role-based features, built-in security, and a user friendly interface. I originally developed this project as part of a small team using Agile-Scrum methodologies, including sprint planning, biweekly stand-ups, and detailed documentation (Javadocs, UML diagrams, and system tests). I’m now continuing to expand the project alongside other peers who are using it as a foundation to explore new frameworks, add functionality, and enhance the system’s overall capabilities. 

🔧 Features
- 🔐 Secure login with JWT authentication & role-based access (Customers, Staff, Managers, Admins)
- 👥 Role-based access with custom features and UI for Customers, Staff, Managers, and Admins
- 🧾 User, Inventory, Ingredient, Recipe, History, and Order Management Features
- 🧪 Reliable performance with 90%+ automated test coverage (JUnit & Jest)
- 📊 Manager analytics dashboard (in progress)
  
🧰 Tech Stack
- ☕ Backend: Java, Spring Boot (REST, JWT), JUnit
- ⚛️ Frontend: JavaScript, React, Jest
- 🛠️ Build: Maven, Node.js, npm
- 📈 Database: MySQL (Automated Testing), Amazon Aurora (Application)
- 🗄️ Logs: AWS CloudWatch

Ongoing development includes enhanced analytics for managers and deeper AWS cloud service integration.


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
1. Install Node.js, jdk-17, and Apache-Maven (make sure to test that they work)
2. Install MySQL Workbench and make sure it works, store the root password for later use
3. Setup an AWS Console Account
4. (Optional) Do the following if you would like to create and use a non-root user:
     - On the root account, search for the IAM service in AWS
     - Under Access Management select Policies
     - Click Create Policy, select Json, and paste the snippet belkow, and give the policy a name, then click Create Policy.
{
	"Version": "2012-10-17",
	"Statement": [
		{
			"Effect": "Allow",
			"Action": [
				"ec2:DescribeSecurityGroups",
				"ec2:DescribeSecurityGroupRules",
				"ec2:AuthorizeSecurityGroupIngress",
				"ec2:RevokeSecurityGroupIngress"
			],
			"Resource": "*"
		}
	]
}
     -  Now go to Access Management > User Groups, and select Create Group
     -  Give the group a name and give it the following permissions
AmazonAPIGatewayAdministrator
AmazonDynamoDBFullAccess
AmazonQDeveloperAccess
AmazonRDSFullAccess
AmazonS3FullAccess
AWSLambda_FullAccess
CloudWatchFullAccess
CloudWatchFullAccessV2
IAMReadOnlyAccess 
     -  Finally search for the custom policy you made and add it to the permission list, then click create user group
     -  Now Go to Access Management > Users > Select Create User
     -  give them a name and access to AWS Management Console and select Create an IAM User
     -  In Setpermissions, asign them to the User Group you just made > Now you can freely create the user
     -  Final step is to login to the new account you made for the upcoming steps!
5. Click your AWS Profile > Security Credentials > Create Access Key > Local code 
6. Store the access key locally in environment variables
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
7. Search for the EC2 Service in AWS > go to Network & Security > Security Groups > Right click Inbound Rules Count and select Edit Inbound Rules
8. Put MySQL.Aurora for type and MyIP for Source and create the rule
9. Search for the Aurora and RDS Service in AWS, Select Databases > Click Create Database > Select Following:
Engine Options: Select Aurora (MySQL Compatible) for engine
Template: Dev/Test
Settings DB cluster identifier: extendedmanager
Credential Settings: set username and password, keep them
Public Access: yes
10. Log onto MySQL Workbench > click + sign next to MySQL Connections
11. Put any connection name, put the Aurora credentials in you set up, verify the port is 3306, and finally click ok.
12. CREATE DATABASE extendedmanager;
13. In AWS, search and go to the Cloudwatch Service > Logs > Log groups > Create a log group
14. Name it SpringBootLogs and click create (Note: You can change the name, but you must have it match name in main/resources/logback.xml and main/java/wolfcafe/logs/CloudWatchAppender.java)
15. Click the Log Group just made > Log streams > Create log stream > Name it local stream and click create (Note: You can change the name, but you must have it match name in main/resources/logback.xml and main/java/wolfcafe/logs/CloudWatchAppender.java)
    
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
5. Install Lombok [instructions for setting up Lombok in Eclipse](https://projectlombok.org/setup/eclipse). 
6. In src/main/resources copy the 3 properties.template files and paste them in the folder with .template removed
7. In application.properties do the following: 
  - go to # SHA256 encryption - https://emn178.github.io/online-tools/sha256.html put in any phrase and paste result in jwt-scret
  - put a password for default admin account (you will use this to login)
8. In application-cloud.properties do the following:
  - Put in the username and password that you set for Aurora during Environment Setup
  - For the url, you will paste into the endpoint the endpoint path given in AWS Console > Aurora and RDS Service > Databases > extendedmanager > use writer endpoint one for path
9. In application-localtest.properties put the password you used when making your MySQL account. 
10. Rightclick the pom.xml file and select Maven > Update Project then click Ok.
11. Run the JUnit test by right-clicking src/test/java > Run as > Junit Test, it should pass ALL test. (Note: Sometimes it messes up running so many test at once, if a class fails just go to the individual folder and run the test cases, this commonly fixes it!) 
12. Run the backend by right clicking on the project application (WolfCafeApplication.java) and selecting Run As > Java Application. 
    

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
