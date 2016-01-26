/*eslint-env node*/

//------------------------------------------------------------------------------
// node.js starter application for Bluemix
//------------------------------------------------------------------------------

// This application uses express as its web server
// for more info, see: http://expressjs.com
var express = require('express');
var cfenv = require('cfenv');   // cfenv provides access to your Cloud Foundry environment
var https = require('https');
var url = require('url');
var extend = require('util')._extend;
var nodemailer = require("nodemailer");

var Cloudant = require('cloudant');
var me = 'nodejs'; // Set this to your own account 
var password = JSON.stringify(process.env.VCAP_SERVICES);
//console.log(password);
// setup middleware
var app = express();    // create a new express server
var bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({
  extended: true
}));app.use(bodyParser.json());

// serve the files out of ./public as our main files
app.use(express.static(__dirname + '/public'));
//app.set('view engine', 'jade');

//console.log('VCAP SERVICES: ' + JSON.stringify(process.env.VCAP_SERVICES, null, 4));
  if (process.env.VCAP_SERVICES) {
     var VCAP_SERVICES = JSON.parse(process.env.VCAP_SERVICES);
     // retrieve the credential information from VCAP_SERVICES for Cloudant DB
     hostname   = VCAP_SERVICES["cloudantNoSQLDB"][0].name;
     host = VCAP_SERVICES["cloudantNoSQLDB"][0].credentials.host;
     port = VCAP_SERVICES["cloudantNoSQLDB"][0].credentials.port;
     passwd = VCAP_SERVICES["cloudantNoSQLDB"][0].credentials.password; 
     username = VCAP_SERVICES["cloudantNoSQLDB"][0].credentials.username; 
    cloudant_url = VCAP_SERVICES["cloudantNoSQLDB"][0].credentials.url;
     
  console.log("********************************************");
  console.log("Host:" + hostname + "  Password:" + passwd );
    console.log("Username:" + username + " Port:"+ port);
    console.log("********************************************");
  
  //use the legalwise DB
  
  }
else{
  //hard code the credentials
cloudant_url = "https://69a96372-5c5c-452e-8794-3c67a1257ce5-bluemix:2591a08afce5c71960f478c5a50c44120de77c9f807d49fba6a2ae59c9a3e061@69a96372-5c5c-452e-8794-3c67a1257ce5-bluemix.cloudant.com";
} 

var smtpTransport = nodemailer.createTransport("SMTP",{
    service: "Gmail",
    auth: {
        user: "legalwise.jaimeborras@gmail.com",
        pass: "LegalWise2015"
    }
});
var rand,mailOptions,host,link;
var fullname;
app.get('/send',function(req,res){
        rand=Math.floor((Math.random() * 100) + 54);
    host=req.get('host');
    fullname = req.query.fname +" "+req.query.lname;
    console.log(fullname);
    link="http://"+req.get('host')+"/verify?id="+rand;
    mailOptions={
        to : req.query.to,
        subject : "Please confirm your Email account",
        html : "Hello " +fullname+ ",<br><br> Please Click on the link to verify your email.<br><a href="+link+">Click here to verify</a>" 
    }
    console.log(mailOptions);
    smtpTransport.sendMail(mailOptions, function(error, response){
     if(error){
            console.log(error);
        res.end("error");
     }else{
            console.log("Message sent: " + response.message);
        res.end("sent");
         }
});

});
var home = "/index.html";
app.get('/verify',function(req,res){
console.log(req.protocol+":/"+req.get('host'));
if((req.protocol+"://"+req.get('host'))==("http://"+host))
{
    console.log("Domain is matched. Information is from Authentic email");
    if(req.query.id==rand)
    {
        console.log("email is verified");
        res.end("<h1>Email "+mailOptions.to+" is been successfully verified. Please click<a href="+home+"> here to login</a>.");
    }
    else
    {
        console.log("email is not verified");
        res.end("<h1>Bad Request</h1>");
    }
}
else
{
    res.end("<h1>Request is from unknown source");
}
});

  var nano = require('nano')(cloudant_url);   //connect nano to DB
  var database = nano.db.use('legalwise'); 

app.post('/createUser', function(req, res){

    var email = req.body.email;
    var fname = req.body.fname;
    var lname = req.body.lname;
    var concentration = req.body.concentration;
    var law_firm = req.body.lawfirm;
    var position = req.body.position;
    var telephone = req.body.telephone;
    var password = req.body.password;
    var re_password = req.body.re_password;
    var userId = email;
var json = {user:{}};

json['id'] = email;
json.user['first_name'] = fname;
json.user['last_name'] = lname;
json.user['gender'] = "male";
json.user['law_firm'] = law_firm;
json.user['position'] = position;
json.user['telephone'] = telephone;
json.user['password'] = password;
json.user['re_password'] = re_password;
json.user['concentration'] = concentration;

json = JSON.stringify(json);
var final_json = JSON.parse(json);


database.insert(final_json, userId, function(err, body, header) {
   
    if (!err) {       
     res.send("Thank you " + fname + " " +lname + " for registering, please check your email to confirm.");
    console.log("Test Case for Create Account Passed!");
    //res.sendfile('./public/accountSuccessful.html');
  //res.set('Content-Type', 'application/javascript');
 // res.sendfile('./public/accountSuccessful.html', {username: [{"first_name": fname, "last_name": lname}]});

  }
  else{
     
     if(err.error == "conflict")
      res.sendfile('./public/accountFail.html');
  }
  }); 
    
});
app.post('/loginUser', function(req, res){
var email = req.body.useremail;
var password = req.body.password;
database.get(email,function(err, body){
//var temp=req.query.useremail;
//console.log("My name is: " +temp);
if(!err){
  if(password == body.user.password){
    console.log("Test Case for Login Passed!");
  res.sendfile('./public/welcome.html');
  //res.end("<h1>Welcome back "+temp+" . Please click<a href="+home+"> here to access LegalWise</a>.");

}
else{
  res.sendfile('./public/loginFail.html');
}}
else{

   res.sendfile('./public/loginFail.html');
}
});

});


// render index page
app.get('/', function(req, res){
    res.render('index');
});
app.get('/', function(req, res){
    res.render('home');
});
app.get('/', function(req, res){  
    res.render('login');
});
app.get('/', function(req, res){
    res.render('support');
});
app.get('/', function(req, res){
    res.render('createAccount');
});
app.get('/', function(req, res){
    res.render('about');
});
/*
xml, uml, j2EE, databases. design patterns. exectives requirements. how would they customize the software, 
child wealthfare, social workers. java entriprse, tweaking.   
7203959110 - ext. 
*/
// *get the app environment from Cloud Foundry
var appEnv = cfenv.getAppEnv();

// start server on the specified port and binding host
app.listen(appEnv.port, function() {

  // print a message when the server starts listening
  console.log("server starting on " + appEnv.url);
});
