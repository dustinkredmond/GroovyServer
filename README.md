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

    git clone https://github.com/dustinkredmond/GroovySerer
    cd GroovyServer
    mvn package`

**2. Create a MariaDB or MySQL schema/database for the installation.** 

    mysql> create database groovy_server;

**3. Create a user for GroovyServer and give them permissions.**

    grant all privileges on groovy_server.* to 'username'@'localhost' identified by 'aGoodPassword';

**4. Deploy the WAR file to a Tomcat Application server**

   Example, from the `target` directory, after build
   
   Your path to Tomcat may be different.
   
   `mv ./GroovyServer.war /var/lib/tomcat9/webapps/GroovyServer.war`
   
   If Tomcat doesn't automatically expand your WAR,
   create a directory, `mv` the WAR into it, then extract.
   
   `user@host: /var/lib/tomcat9/webapps/$ sudo mkdir ./GroovyServer`
   
   `sudo mv ./GroovyServer.war ./GroovyServer/`
   
   `sudo jar xvf ./GroovyServer/GroovyServer.war`
   
   `sudo rm ./GroovyServer/GroovyServer.war`

**5. Navigate to the deployed (unpacked) application and change the database connection details.**

   We must configure the following in `META-INF/context.xml`
   
   - Database username, password, and connection URL
   - All the above are required
      
    `vi /var/lib/tomcat9/webapps/GroovyServer/META-INF/context.xml`
    
   After changing `context.xml` we must reload Tomcat.
    
    `service tomcat9 restart` or `service tomcat9 reload`
    
**6. Navigate to the URL where you deployed GroovyServer**

   Login with the default admin credentials (username: admin password: admin).
   Make sure to change these credentials before production use.

   You are now ready to use GroovyServer. Start by creating
   a script from the `Groovy Scripts` page. Available options
   are accessible under the `Actions` menu. Scripts can be 
   run on demand or on a schedule.

---

### Is GroovyServer safe?

Disclaimer time. While we do everything possible to make sure GroovyServer is 
bulletproof, if given enough time, anyone can hack anything.

Since GroovyServer facilitates execution of arbitrary Groovy code, if a hacker
were to compromise the system, this could be catastrophic. They could execute
any code that Groovy/Java would allow.

For the above reason, we recommended an instance of GroovyServer not
be open for use on the WWW, but rather only be accessible via a local intranet.
This way, if someone were to hack into the application, they would at least have
to be on the local network, which requires much more know-how than simply brute-forcing
their way through password based authentication.

That being said, there are instances of GroovyServer on the WWW today, and
(knock on wood) they remain secure and un-compromised. One must weight the 
risks before making any web-application publicly available.

---

Feel free to fork the repo or submit a pull request. We love your input!

