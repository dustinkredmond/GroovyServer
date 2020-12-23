# GroovyServer
### Run Apache Groovy from a web interface!

[![CircleCI](https://circleci.com/gh/dustinkredmond/GroovyServer.svg?style=svg)](https://circleci.com/gh/dustinkredmond/GroovyServer)

GroovyServer lets you run and schedule Apache Groovy code from
a simple web interface. 
---

### Features

 - Dynamically create/change Groovy Scripts from the browser
 - Use created classes within other classes
 - Schedule a script to run with a cron expression
 - Schedule a script to run hourly/daily/monthly/weekly
 - Create easily modifiable variables accessible by Groovy scripts
   - Change the variable, and it will change in all the scripts that use it
   - Great for things like email addresses, URLs, etc
 - Notifications for system events: errors, warnings (this is configurable)
 - Execution log view, see who runs what scripts, and when
   - Your scripts can also log to this view
 - Send emails from scripts using configured SMTP settings
 - GroovyServer bundles the Twilio API
   - Send SMS or voice messages from scripts
 - Easily create CSV reports via built-in helper APIs
 - For scripts executed on demand:
    - Make use of ZKOSS framework UI components
      - Ask for user input when run
      - Display results of a script in the browser
 
---

### Installation instructions

**1. Grab the project and build the WAR file**

    `git clone https://github.com/dustinkredmond/GroovySerer`
    
    `mvn package`

**2. Create a MariaDB or MySQL schema/database for the installation.** 

    E.g. `create database groovy_server;`

**3. Create a user for GroovyServer and give them permissions.**

    `grant all privileges on groovy_server.* to 'username'@'localhost' identified by 'aGoodPassword'`

**4. Deploy the WAR file to a Tomcat Application server**

   Example, from the `target` directory, after build
   
   Your path to Tomcat may be different.
   
   `mv ./GroovyServer.war /var/lib/tomcat9/webapps/GroovyServer.war`

**5. Navigate to the deployed application and change the security passphrase.**

   For security, we have to provide a secure passphrase in `META-INF/context.xml`
    
    `vi /var/lib/tomcat9/webapps/GroovyServer/META-INF/context.xml`
    
   Change the value from `default` to something secure.    
   After changing `context.xml` we must reload Tomcat.
    
    `service tomcat9 restart` or `service tomcat9 reload`
    
**6. Navigate to the URL where you deployed GroovyServer**

   The web interface will prompt for the following:    
    - Database username (created in step 3)
    - Database password (created in step 3)
    - Database Connection URL
      - One of the following if you named your schema `groovy_server`
        - `jdbc:mariadb://localhost:3306/groovy_serer`
        - `jdbc:mysql://localhost:3306/groovy_server`
    - Passphrase from `context.xml` (created in step 5)
    - If you want to create an admin account
      - If this is your first time using GroovyServer, you
        must do this.
        
**7. Revisit the GroovyServer URL and login**

   You are now ready to use GroovyServer. Start by creating
   a script from the `Groovy Scripts` page. Available options
   are accessible under the `Actions` menu. Scripts can be 
   run on demand or on a schedule.

---

Feel free to fork the repo or submit a pull request. We love your input!

