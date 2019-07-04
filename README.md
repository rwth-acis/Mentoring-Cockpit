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
all information of users enrolled in a specified course. For the LL, the email is important, since xAPI statements have the email as key attribute for all actors (students). After setting up the Moodle web server, data can retrieved under <MoodleDomain> /webservice/rest/server.php?token=...&wsfunction=...&moodlewsrestformat=json. A las2peer node initiates the domain, token and the course id. The pa- rameters are transferred with las2peers web connector under <webconnector>/startmoodle/{domain}/{token}/{courseid}. The node then sends a request to moodle every 30 seconds and compares if there are any changes. If something changed in Moodle a monitoring event is issued, which stores the JSON string that contains the Moodle data in the MobSOS database under a special service custom message flag for each function.


# MobSOS to Learning Locker
The MobSOS data processing services stores the MobSOS messages in a MySQL database. This database then can be accessed to retrieve the MobSOS monitor- ing events. A Javascript application handles the connection between the MobSOS database and the LL, since the easiest way to send and create xAPI statements is through the officially supported TinCanJS Library (and because a connection with a Java application turned out to be difficult). Node.js is used a driver for the connection to the MySQL database. First, the most resent message with the Moodle flag is selected. Then a JSON parser extract following data: course id, name and id of the student, for every grade item the name, id, module, date of submission, percentage of achieved points and feedback. For every grade item an xAPI statement is generated which utilizes this data. The statement is then sent to the LL
