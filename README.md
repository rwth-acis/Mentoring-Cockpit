# Mentoring-Cockpit
Repository for Thesis work 'A Multimodal Mentoring Cockpit for Tutor Support'

# Moodle to MobSOS
To get Moodle data to MobSOS first a rest api has to be created on Moodle. This can be achieved by creating a Moodle web service under following steps:
- Enable web services under Administration > Site administration > Advanced features
- Enable REST Protocols under Administration > Site administration > Plugins > Web services > Manage protocols
- Create a new service Administration > Site administration > Plugins > Web services > External services
- Add functions to the new service.
- Create a token for the service under Administration > Site Administration > Plugins > Web services > Manage tokens

On https://docs.moodle.org/dev/Web_service_API_functions a list of all Moodle web service API functions can be found. The function gradereport user get grade items that returns a complete list of grade items and for users in a specified course, but without the user’s email address. Therefore, the function core enrol get enrolled users gets
all information of users enrolled in a specified course. For the LL, the email is important, since xAPI statements have the email as key attribute for all actors (students). After setting up the Moodle web server, data can retrieved under <MoodleDomain> /webservice/rest/server.php?token=...&wsfunction=...&moodlewsrestformat=json. A las2peer node initiates the domain, token and the course id. To start a the moodle connection for a course call <webconnector>/start-moodle/{courseid}. The node then sends a request to moodle every 30 seconds and compares if there are any changes. If something changed in Moodle a monitoring event is issued, which stores the JSON string that contains the Moodle data in the MobSOS database under a special service custom message flag for each function.
See [property file](etc/i5.las2peer.services.mentoringCockpitService.MentoringCockpitService.properties) to configure the domain and the token of the Moodle instance.

# MobSOS to Learning Locker
The MobSOS data processing services stores the MobSOS messages in a MySQL database. When an xAPI statement is stored in the remarks of a message a new statement, a service is invoked which passes the xAPI statement onto an LRS. See [property file](etc/i5.las2peer.services.mentoringCockpitService.MentoringCockpitService.properties) to configure the address and authentication of the LRS.