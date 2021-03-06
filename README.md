IST App Market
==============

**Table of Contents**

- [About](#about)
- [Configuration](#Configuration)
- [Run](#Run)

##About
This is a platform where students of IST can upload and/or download applications that use FénixEdu API. 
You can also search for applications that work online (for browsers)
Each app has a description, comments and vote.

##Configuration
1. Download the latest version of play framework from http://www.playframework.com/download
2. Rename the application configuration `mv conf/application.conf.template conf/application.conf`
3. Open `conf/application.conf` and edit the database configuration
    `db.default.url="jdbc:mysql://<db-server>/<db-name>"`    
    `db.default.user=<username>`    
    `db.default.password=<password>`    
4. Create a file named `configuration.properties` and specify both your Consumer Key and Secret
    `fenixedu.oauth.consumer.key=<your-consumer-key>`
    `fenixedu.oauth.consumer.secret=<your-consumer-secret>`
    `fenixedu.callback.url=<your-application-callback-url>`
    `fenixedu.base.url=<fenixedu-installation-base-url>`
   Put this file in `conf/` folder.

##Run
To run the application type in terminal

`play run`

for more commands see play framework documentation.

