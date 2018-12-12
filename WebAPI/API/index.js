var express = require('express');
var bodyParser = require('body-parser');
var mongoose = require('mongoose');
var jwt = require('jsonwebtoken');
var qrcode = require('qrcode')

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended:true}));
app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    next();
  });

mongoose.connect('mongodb://beacondb:beacondb@nodecluster-shard-00-00-ldy3x.mongodb.net:27017,nodecluster-shard-00-01-ldy3x.mongodb.net:27017,nodecluster-shard-00-02-ldy3x.mongodb.net:27017/project?ssl=true&replicaSet=nodecluster-shard-0&authSource=admin&retryWrites=true',
{useNewUrlParser:true});


// Todo
// QR codes for teams and users
// 

var userSchema = mongoose.Schema({
    userId : String,
    name : String,
    password:String,
    qrcodeAddress:String
    
})

var teamSchema = mongoose.Schema({

    teamId: String,
    evaluationscount : Number,
    score:mongoose.Schema.Types.Decimal128,
    qrcodeAddress:String

})

var teamScores = mongoose.Schema({
    userId : String,
    teamId: String,
    score:mongoose.Schema.Types.Decimal128
})

var questionsschema = mongoose.Schema({

    questionId:Number,
    question: String
})

var adminschema = mongoose.Schema({
    username:String,
    password:String
})

var questionscountschema = mongoose.Schema({
    count:Number
})

var User = mongoose.model('Users',userSchema)
var teams = mongoose.model('Teams',teamSchema)
var teamscores = mongoose.model('TeamScores',teamScores)
var surveyquestions = mongoose.model('Survey Questions',questionsschema)
var admin = mongoose.model('admin',adminschema)
//var questionscount = mongoose.model('questionscount',questionscountschema)

// login User

app.get('/login',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else if (authData.type != 'user')
        {
            console.log('not user')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
            res.status(200).json({
                message:'Login successful',
                userid:authData.id,
                type:authData.type
            });

        }

})
})

app.post('/adminadd',function(req,res){

    var Admin = new admin({
        username:req.body.username,
        password:req.body.password
    })
    Admin.save().then(result =>{
        res.status(200).json({
            "message":"Admin added successfully"
        })
    }).catch(err =>{
        res.status(200).json({
            "message":"Error adding Admin"
        })
    })
})

app.post('/adminlogin',function(req,res){

    //console.log(" inside admin login")

    var userName = req.body.username
    var payload = {
        id: userName,
        type:'admin'
    }
    var tokenValue = jwt.sign(JSON.parse(JSON.stringify(payload)), 'secretkey',{
            
    });
    admin.find({username:userName}).then(result =>{

         if (result[0].password == req.body.password)
         {
             res.status(200).json({
                 "token":tokenValue
             })
         }
    })
    
})

// Login team

app.get('/loginteam',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'team')
        {
            console.log('not team')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
            res.status(200).json({
                message:'Login successful',
                teamid:authData.id
            });

        }

})
})

// Add an user
app.post('/user',verifyToken,function(req,res)
{
    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin')
        {
            console.log('not admin')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
     User.find({userId:req.body.id}).then(result => {

         if (result.length == 0)
         {
            var payload = {
                //password: req.body.password,
                id: req.body.id,
                type:'user'
            }
            var tokenValue = jwt.sign(JSON.parse(JSON.stringify(payload)), 'secretkey',{
            
            });
            qrcode.toDataURL(tokenValue, function (err, url) {
                // console.log(url)
            
             var addUser = new User(
                 {
                       userId : req.body.id,
                       name:req.body.name,
                       password : req.body.password,
                       qrcodeAddress:url
                 })

                 addUser.save().then(result =>
                    {
                        res.status(201).json({
                            message:"User added successfully",
                            createdProduct:addUser
                        });
                    }).catch(err =>{
                        res.status(500).json({error:err});
                    })
                });
         }
         else{
            res.status(500).json({
                message:"User Already Exists",
            });
         }
     });
    }
})
})

// add a team
app.post('/team',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin')
        {
            console.log('not admin')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{

    teams.find({teamId:req.body.id}).then(result => {

        console.log(result.length)

    if (result.length == 0) {

        var payload = {
            //password: req.body.password,
            id: req.body.id,
            type:'team'
        }
        var tokenValue = jwt.sign(JSON.parse(JSON.stringify(payload)), 'secretkey',{
            
        });
        qrcode.toDataURL(tokenValue, function (err, url) {
        var team = new teams({

            teamId: req.body.id,
            evaluationscount:0,
            score:0.0,
            qrcodeAddress:url

        })
    team.save().then(result =>{
     
        res.status(201).json({
            message:"Team Created successfully",
            createdTeam:team,
            token:tokenValue
        });
    }).catch(err =>{
        res.status(500).json({error:err});
    })
})
}
else {
    res.status(500).json({
        message:"Team Already Exists"
    });
}
    })
}

})
})


// Add or Update Score

app.get('/score',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'user')
        {
            console.log('not user')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{

    User.find({userId:req.query.userId}).then(result =>{
    

        if (result.length == 0){
            res.status(500).json({
                message:"Invalid user"
            });
        }else {
        
            teams.find({teamId:req.query.teamid}).then(result =>{

                if (result.length == 0){

                    res.status(500).json({
                        message:"Invalid team"
                    });
                }else {
                    
                    var score = result[0].score
                    var ecount = result[0].evaluationscount
                    console.log('score is ' + score)
                    console.log('req score is ' + req.query.score)
                    teamscores.find({userId:req.query.userId,teamId:req.query.teamid}).then(inresult =>{

                        if (inresult.length == 0){
            
                            console.log('no')
                            var updatedScore = (parseFloat(score) + parseFloat(req.query.score))
                             console.log('no updated score is ' + updatedScore)
                            var teamscore = new teamscores({
                                userId : req.query.userId,
                                teamId: req.query.teamid,
                                score:req.query.score
                            })
                            teamscore.save().then(result =>{})
                            teams.updateOne({teamId:req.query.teamid},{$set:{score:updatedScore,evaluationscount:parseInt(ecount)+1}}).then(result =>{
            
                                res.status(201).json({
                                    message:"Score Updated successfully",
                                });
                            }).catch(err =>{
                                res.status(500).json({error:err});
                            })
                        }
                        else {
                            console.log('yes')
                            var previousscore = inresult[0].score
                            console.log('previous score '+previousscore)
                            var fscore = parseFloat(score) - parseFloat(previousscore) + parseFloat(req.query.score)
                             console.log('yes updated score is ' + fscore)
                            teamscores.updateOne({userId:req.query.userId,teamId:req.query.teamid},{$set:{score:req.query.score}}).then(result =>{
                
                            teams.updateOne({teamId:req.query.teamid},{$set:{score:fscore}}).then(result =>{
            
                                res.status(201).json({
                                    message:"Score Updated successfully",
                                });
                            }).catch(err =>{
                                res.status(500).json({error:err});
                            })
                        })
                        }
                })
                }
            })
       
        }
    })
}

})
})

// Get all scores
app.get('/allscores',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin' && authData.type != 'user')
        {
            console.log('not admin or user')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
    teams.find().sort({"score":-1}).then(result =>{
        
        res.status(200).json({
            data:result
        })
    })
}
})
})

// Get all users (Evaluators)
app.get('/users',verifyToken,function(req,res){

    // console.log("inside users")

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin')
        {
            console.log('not admin')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
   User.find().sort({"userId":1}).then(result =>{

    // console.log("result is"+ result)
    res.status(200).json({
        data:result
    })
   })
}
})
})

// get alll teams
app.get('/teams',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin')
        {
            console.log('not admin')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
    teams.find().then(result =>{
        res.status(200).json({
            data:result
        })
    })
}
})
})

// add questions
app.post('/questions',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin')
        {
            console.log('not admin')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
    
    surveyquestions.find().then(result =>{

    var question = new surveyquestions({

        questionId:result.length + 1,
        question:req.body.question
    })
    question.save().then(result =>{
        teamscores.deleteMany().then(result => {
            teams.updateMany({},{$set:{evaluationscount:0,score:0}}).then(result =>{
                res.status(200).json({
                    message:"question deleted succesfully"
                })
            })
           
        })
    }).catch(err =>{
        res.status(500).json({
            error:err
        })
    })
})
        }
    
})
})

app.post('/count',function(req,res,next)
{
    var qcount = new questionscount({
        count:9
    })
    qcount.save().then(result =>{
        res.status(200).json({
            message:"count added succesfully"
        })
    }).catch(err =>{
        res.status(500).json({
            error:err
        })
    })
})

//delete questions
app.delete('/questions',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin')
        {
            console.log('not admin')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
    surveyquestions.deleteMany({"question":req.body.questionId}).then(result=>{
        teamscores.deleteMany().then(result => {
            teams.updateMany({},{$set:{evaluationscount:0,score:0}}).then(result =>{
                res.status(200).json({
                    message:"question deleted succesfully"
                })
            })
           
        })
    })
}
})
})

// get all survey questions
app.get('/questions',verifyToken,function(req,res){

    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin')
        {
            console.log('not admin')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{
    surveyquestions.find().sort({questionId:1}).then(result =>{

        res.status(200).json({
            data:result
        })
    })
}
})
})

app.get('/userteams' , verifyToken,function(req,res,next)
{
    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin' && authData.type !='user')
        {
            console.log('not admin or user')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{

            User.find({userId:req.query.userId}).then(result =>{
    

                if (result.length == 0){
                    res.status(500).json({
                        message:"Invalid user"
                    });
                }else {
                    teamscores.find({userId:req.query.userId}).then(result =>{
                        console.log("result is " + result);
                        res.status(200).json({
                            teams:result
                        })
                    })

                }
            })
        }
})
})

// get user scores teams

app.get('/userevaluations' , verifyToken,function(req,res,next)
{
    console.log(' request is ' + req.query.userId)
    jwt.verify(req.token,'secretkey',(err,authData) =>{

        if (err){
            res.status(403).json({
                error:'Invalid Token'
            });
        }else if (authData.type != 'admin' && authData.type !='user')
        {
            console.log('not admin or user')
            res.status(403).json({
                error:'Invalid Token'
            });
        }
        else{

            User.find({userId:req.query.userId}).then(result =>{
    

                if (result.length == 0){
                    res.status(500).json({
                        message:"Invalid user"
                    });
                }else {
                    teamscores.find({userId:req.query.userId}).then(result =>{
                        console.log("result is " + result);
                        res.status(200).json({
                            data:result
                        })
                    })

                }
            })
        }
})
})

// Get JWT Token
app.get('/usertoken',function(req,res)
{
    var payload = {
        //password: req.body.password,
        id: req.query.id
    }
    var token = jwt.sign(JSON.parse(JSON.stringify(payload)), 'secretkey',{
        
    });
    res.status(200).json({"jwt":token})
})

// verify token and send back details
app.get('/verify',verifyToken,function(req,res)
{
    jwt.verify(req.token,'secretkey',(err,authData) =>{
        if (err){

            res.status(403).json({
                "error":"Invalid token"
            });
        }
        else 
        {
        var decoded = jwt.decode(req.token, {complete: true});
        // console.log(decoded.header)
        console.log(decoded.payload)
        res.status(200).json({
            'id':decoded.payload.id,
            'type':decoded.payload.type
        })
        }
    })
})

// verify token function

// token format - Bearer eyJhbGciOiJIUzI1NiJ9.cGFzc3dvcmQ.LZJl4t4Cc1bFh9ok8lenPGWtaYNfxztqKfBSCyJcr60

function verifyToken(req,res,next){
    //Get auth header value
    const bearerHeader=req.headers['authorization'];
    //console.log(bearerHeader)
    //CHECK IF Bearer is undefined
    if(typeof bearerHeader!=='undefined')
    {
        const bearer = bearerHeader.split(' ');
        const bearertoken = bearer[1];
        req.token=bearertoken;
        next();
    }else{
     //FORBIDDEN
        res.status(403).json({
            "error":"Invalid token"
        });
    }
}

app.listen(4000,()=>{console.log("server is running at 4000")});
