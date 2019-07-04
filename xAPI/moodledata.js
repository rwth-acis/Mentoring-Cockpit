var mysql = require('mysql');

var con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "123",
  database: "LAS2PEERMON"
});

/*con.connect(function(err) {
    if(err) throw err;
});*/

var sqlGrades = "SELECT REMARKS "
        + "FROM MESSAGE "
        + "WHERE EVENT = 'SERVICE_CUSTOM_MESSAGE_1' "
        + "AND REMARKS LIKE '%usergrades%'"
        + "ORDER BY TIME_STAMP DESC LIMIT 1";

var sqlEmail = "SELECT REMARKS "
        + "FROM MESSAGE "
        + "WHERE EVENT = 'SERVICE_CUSTOM_MESSAGE_2' "
        + "AND REMARKS LIKE '%email%'"
        + "ORDER BY TIME_STAMP DESC LIMIT 1";


var getEmail = function(callback) {
  con.query(sqlEmail, function (err, result, fields) {
    if(err) callback(err);

    var userinfos = JSON.parse(result[0].REMARKS);
    var userDict = {};
    for (var i = 0; i < userinfos.length; i++) {
      var userinfo = userinfos[i];
      userDict[userinfo.id] = userinfo.email;

    }
    callback(null, userDict);
  });
}

var getEverything = function(callback) {
  
  var everythingArr = [];
  getEmail(function (err, userDict) {
    if(err) console.log("Error");
  
    con.query(sqlGrades, function (err, result, fields) {
      if(err) throw err;

      var gradereport = JSON.parse(result[0].REMARKS); 

      for (var i = 0; i < gradereport.usergrades.length; i++) {
        var usergrade = gradereport.usergrades[i];
        
        var courseid = usergrade.courseid;
        var username = usergrade.userfullname;
        var userid = usergrade.userid;
        var email = userDict[userid]
        //the last entry in usergrade.gradeitems is a summary and we don't need this data
        for (var j = 0; j < usergrade.gradeitems.length-1; j++) {
          var gradeitem = usergrade.gradeitems[j];

          var itemname = gradeitem.itemname
          var itemid = gradeitem.id;
          var itemmodule = gradeitem.itemmodule;
          var gradedatesubmitted = gradeitem.gradedatesubmitted;
          var percentageformatted = gradeitem.percentageformatted;
          var feedback = gradeitem.feedback;
          
          
          //console.log(courseid, username, userid, email, itemname, itemid, itemmodule, gradedatesubmitted, percentageformatted, feedback);
        }
        //todo find better solution 
        everythingArr[i] = [courseid, username, userid, email, itemname, itemid, itemmodule, gradedatesubmitted, percentageformatted, feedback];
      }
      callback(null, everythingArr);
    });
  });
}


getEverything(function (err, everythingArr) {
  if(err) console.log("Error");
  console.log(everythingArr[0]);
  console.log(everythingArr[1]);
  
});

