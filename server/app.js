var express    = require('express'),
    validator  = require('express-validator'),
    app        = express(),
    bodyParser = require('body-parser');

// configure app to use bodyParser() which will get the data from a POST
app.use(require('express-method-override')());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(validator);
app.use(express.static(__dirname + '/public'));

var port = process.env.PORT || 8080;

// REGISTER ROUTES
app.use('/', require('./routes').router);

// START THE SERVER
app.listen(port);
console.log('--Server started at port: '+port+"--");




