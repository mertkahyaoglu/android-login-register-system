var mysql = require('mysql'),
    db = null;
module.exports = function () {
    if(!db) {
        db = mysql.createConnection({
            host:       "localhost",
            user:       "root",
            password:   "",
            database:   "dbname"
        });
    };
    return db;
};
