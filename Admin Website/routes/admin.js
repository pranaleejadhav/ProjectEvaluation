var express = require('express');
var router = express.Router();
var bodyParser = require('body-parser');
var request = require('request');
var jade = require('jade');
var check = require('./check');
var qrcode = require('qrcode')
var app = express();
var token;
var userName;

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));

/* GET home page. */
router.get('/',check.sessioncheck,function(req, res, next) {
  res.render('index',{title:'Admin'});
});

router.get('/home', check.logged,function(req,res,next){
  res.render('dashboard',{title:'Dashboard'});
})

router.post('/dashboard',check.sessioncheck,function(req,res,next){

  var json = {
    "username": req.body.emailaddress,
    "password": req.body.passwordvalue
  };

  // console.log("json is " + json.username)
  // console.log("json is " + json.password)

  var options = {
    //url: 'http://ec2-18-221-45-243.us-east-2.compute.amazonaws.com:9000/admin',
    url: 'http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/adminlogin',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    json: json
  };
  request(options, function(err, resp, body) {
    //console.log("inside request");
    if (resp && (resp.statusCode === 200)) {
      token = 'Bearer ' + body.token;
      //console.log(" token is " + token)
      req.session.user = {
        "tokenValue" : token,
      };
      res.redirect('/admin/home');
    }
    else {
      //console.log("inside false");
      res.redirect('/admin');
    }
});     
});


router.get('/manageevaluators',check.logged,function(req,res,next){
  
  var data = {
    title:'Evaluators Details',
    tokenValue: token
  };
  res.render('evaluator',data);
});

router.get('/manageteams',check.logged,function(req,res,next){

  var data = {
    title:'Teams Details',
    tokenValue: token
  };
  res.render('teams',data);
})

router.get('/addquestion',check.logged,function(req,res,next)
{
  console.log('inside add question')
  console.log('request is ' + req)
  var data = {
    title:'Add Question',
    tokenValue: token
  };
  res.render('addquestion',data); 
})

router.get('/reviewevaluations', check.logged,function (req, res) {
  // QRCode.toDataURL('I am a pony!', function (err, url) {
  //  console.log(url)
  //  res.render('index', {qr: url});
  //  });
  var data = {
    title:'Score Board',
    tokenValue: token
  };
  res.render('scores',data);
 }); 

router.get('/addEvaluator',check.logged,function(req,res){
  var data = {
    title:'Add Evaluator',
    tokenValue: token
  };
  res.render('register',data); 
})
router.get('/addteam',check.logged,function(req,res){
  var data = {
    title:'Add Team',
    tokenValue: token
  };
  res.render('registerteam',data); 
}) 
router.post('/registerevaluator',check.logged,function(req,res){

  console.log("user name is " + req.body.username)
  var json = {
    "id":req.body.username,
    "name": req.body.fname + " "+req.body.lname,
    "password": req.body.psw
  };
  console.log("registre"+json.id +json.name + json.password);
  // console.log("json is " + json.username)
  // console.log("json is " + json.password)

  var options = {
    //url: 'http://ec2-18-221-45-243.us-east-2.compute.amazonaws.com:9000/admin',
    url: 'http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/user',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization':token
    },
    json: json
  };
  request(options, function(err, resp, body) {
    console.log("inside request");
    if (resp && (resp.statusCode === 201)) {
      res.redirect('/admin/manageevaluators');
    }
    else {
      console.log("inside false");
      console.log(err);
      res.redirect('/admin/addEvaluator');
    }
});     

})
router.post('/registerteam',function(req,res){

  var json = {
    "id":req.body.fname,
  };

  var options = {
    //url: 'http://ec2-18-221-45-243.us-east-2.compute.amazonaws.com:9000/admin',
    url: 'http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/team',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization':token
    },
    json: json
  };
  request(options, function(err, resp, body) {
    if (resp && (resp.statusCode === 201)) {
      res.redirect('/admin/manageteams');
    }
    else {
      res.redirect('/admin/addteam');
    }
});     

})
router.post('/registerquestion',check.logged,function(req,res,next)
{
  var json = {
    "question":req.body.fname,
  };

  var options = {
    //url: 'http://ec2-18-221-45-243.us-east-2.compute.amazonaws.com:9000/admin',
    url: 'http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/questions',
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization':token
    },
    json: json
  };
  request(options, function(err, resp, body) {
    if (resp && (resp.statusCode === 200)) {
      res.redirect('/admin/editsurvey');
    }
    else {
      res.redirect('/admin/registerquestion');
    }
});  
})
router.get('/editsurvey', check.logged,function(req,res){
  var data = {
    title:'Survey Questions',
    tokenValue: token
  };
  
  res.render('editsurvey',data);
})

router.get('/deletequestion',check.logged,function(req,res,next)
{
  if (req.query.questionId != undefined)
  {
  console.log('inside delete')
  console.log('clicked ' + req.query.questionId)
  var json = {
    "questionId":req.query.questionId,
  };

  var options = {
    //url: 'http://ec2-18-221-45-243.us-east-2.compute.amazonaws.com:9000/admin',
    url: 'http://ec2-18-216-57-132.us-east-2.compute.amazonaws.com:4000/questions',
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'Authorization':token
    },
    json: json
  };
  request(options, function(err, resp, body) {
    if (resp && (resp.statusCode === 200)) {
      res.redirect('/admin/editsurvey');
    }
    else {
      res.redirect('/admin/editsurvey');
    }
});  
  }
  else 
  {
    res.redirect('/admin/editsurvey');
  }
})


router.get('/logout',check.logged,function(req,res,next){

  if(req.session){
    if(req.session.user != null || req.session.user != 'undefined'){
        req.session.user = null;
    }

    req.session.destroy(function () {
        res.clearCookie('user_sid', {
            httpOnly: true
        });
        res.redirect('/admin');
    });
} else{
    res.clearCookie('user_sid', {
        path: '/',
        httpOnly: true
    });
    res.redirect('/admin');
}
     
});

module.exports = router;
