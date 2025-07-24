# WolfCafe

# WolfCafe Backend Statement/Branch Coverage

![Backend Coverage](.github/badges/jacoco-backend.svg)
![Backend Branch Coverage](.github/badges/jacoco-branches.svg)

# WolfCafe Frontend Statement/Branch Coverage

![Frontend Coverage](.github/badges/coverage-frontend.svg)
![Frontend Branch Coverage](.github/badges/frontend-branches.svg)

# Setup Guide

## Eclipse Setup
Import project into Eclipse
1. Open Eclipse Switch to the Java Perspective (upper right corner).
2. Open Git Repositories view: Window > Show View > Other > Git > Git Repositories
3. Clone or Import repo there
4. Import the Maven Project
 - In Package Explorer select Import Projects or right click and select Import Projects from the context menu.
 - Select Maven > Existing Maven Projects.
 - Browse for the repository directory. The selected directory should end in [repo_name]/coffee_maker.
 - Click Import
 - The project should be in the Package Explorer
5. Install Lombok
Lombok is a library that lets us use annotations to automatically generate getters, setters, and constructors.  For Lombok to work in Eclipse (and other IDEs like IntelliJ or VS Code), you need to set up Lombok with the IDE in addition to including in the pom.xml file.

Follow the [instructions for setting up Lombok in Eclipse](https://projectlombok.org/setup/eclipse).  Make sure you download the laste version of Lombok from [Maven Repository](https://mvnrepository.com/artifact/org.projectlombok/lombok) as a jar file.

Update `application.properties` in `src/main/resources/` and `src/test/resources/`.

  * Set `spring.datasource.password` to your local MySQL password`
  * Set `app.jwt-secret` as described below.
  * Set `app.admin-user-password` to a plain text string that you will use as the admin password.
  
### Set `app.jwt-secret`

We will create a secret key that will be used for JWT authentication.  Think of a secret key phrase.  You'll want to encrypt it using SHA256 encryption.  You can use a tool like:  https://emn178.github.io/online-tools/sha256.html to generate the encrypted text.  Copy that into your `application.properties` file.

6. Make sure your SQL database is set up and running
7. Rightclick the pom.xml file and select Maven > Update Project then click Ok.
8. Run the project by right clicking on the project application and selecting Run As > Java Application.
