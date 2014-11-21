var router     = require('express').Router(),
    db         = require('./connectdb')(),
    formidable = require('formidable'),
    fs         = require('fs-extra'),
    util       = require('util'),
    path       = require('path');

db.connect(function(err) {
    if (err) { console.error(err.stack); return;}
});

router.use(function(req, res, next) {
    console.log('--new request--');
    next(); // visit next routes
});

//ANDROID General routes
router.get('/andro', function(req, res) {
    res.json({ message: 'Welcome to Frames!' });   
});

// LOGIN Route
router.route('/andro/login').post(function(req, res) {
    var tag      = req.body.tag,
        email    = req.body.email,
        password = req.body.password;

    req.checkBody('email', 'not a valid email.').isEmail();
    req.checkBody('password', '8-32 character').len(8, 32);
    var errors = req.validationErrors();

    if (tag == "login") {
        if (errors) {
            res.json({message: errors.msg});
        }else {
            db.query('select * from users where email = ? and password = ?', [email, password], function(err, rows, fields) {
                if (err) throw err;
                
                if (rows.length > 0) {
                    userID  = rows[0].id + "";
                    res.json({success: "1", userid: userID, message: "user logged in"});
                }else {
                    res.json({success: "0", message: "invalid email or password"});
                }
            });
        }
    }else {
        res.json({success: "2", message: "not a login request"});
    }
    
});

// REGISTER Route
router.route('/andro/register').post(function(req, res) {
    var tag      = req.body.tag,
        username = req.body.username,
        email    = req.body.email,
        password = req.body.password;

    req.checkBody('email', 'not a valid email.').isEmail();
    req.checkBody('username', '2-32 character').len(2, 32);
    req.checkBody('password', '8-32 character').len(8, 32);
    var errors = req.validationErrors();

    if (tag === "register") {
        if (errors) {
            res.json({message: errors});
        }else {
        //check whether user exists
        db.query('select * from users where email = ?', [email], function(err, rows, result) {
            if (err) throw err;
            if(rows.length > 0){
                res.json({success: "0", message: "email exists"});
            }else {
                //check username exists
                db.query('select * from users where username = ?', [username], function(err, rows, result) {
                    if (err) throw err;
                    if(rows.length > 0){
                        res.json({success: "2", message: "username exists"});
                    }else {
                        db.query('INSERT INTO users VALUES(null, ?, ?, ?)', [username, email, password], function(err, result) {
                          if (err) throw err;
                          res.json({success: "1", userID: result.insertId, message: "registered"});
                          console.log(result.insertId);
                        });
                    }
                });
            }
        });
        }

    }else {
        res.json({success: "2", message: "not a register request"});
    }

});

// UPLOAD Route
router.route('/andro/upload').post(function(req, res) {
    var form = new formidable.IncomingForm();

    form.parse(req, function(err, fields, files) {
      res.writeHead(200, {'content-type': 'text/plain'});
      res.write('received upload:\n\n');
      res.end(util.inspect({fields: fields, files: files}));
    });

    form.on('progress', function(bytesReceived, bytesExpected) {
        var percentage = (bytesReceived / bytesExpected) * 100;
        console.log(percentage.toFixed(2));
    });

    form.on('error', function(err) {
        console.error(err);
    });

    form.on('field', function(name, value) {
        console.log(value);
    });
    
    form.on('end', function(fields, files) {
        var size = this.openedFiles[0].size,
            tmp = this.openedFiles[0].path,
            filename = this.openedFiles[0].name,
            date = this.openedFiles[0].lastModifiedDate,
            target = path.resolve('./uploads/'+filename),
            extention = path.extname(filename).toLowerCase();
            formatDate = new Date(date);
        if (extention === '.png' || extention === '.jpg' || extention === '.gif') {
            fs.rename(tmp, target, function(err) {
                if (err) throw err;
                console.log("Upload completed!");
            });
        } else {
            fs.unlink(tmp, function (err) {
                if (err) throw err;
            });
        }
    });
 
 
    return;
});

module.exports.router = router;