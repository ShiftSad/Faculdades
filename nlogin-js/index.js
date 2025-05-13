const mysql = require('mysql');
const algorithms = require('./algorithm')
const TABLE_NAME = 'nlogin'

class nLogin {
    constructor(host, username, password, database, callback) {
        this.bcrypt = new algorithms.BCrypt();
        this.sha256 = new algorithms.SHA256();
        this.authme = new algorithms.AuthMe();
        this.argon2 = new algorithms.Argon2();
        this.def_algo = this.bcrypt;
        this.con = mysql.createConnection({
            host: host,
            password: password,
            user: username,
            database: database
        })
        this.con.connect((err) => {
            callback(err)
        })
    }

    /**
     * Retrieves the hash associated with the given user from the database.
     *
     * @param {string} username the username whose hash should be retrieved
     * @return {string|null} the hash, or null if unavailable (e.g. username doesn't exist)
     */
    getHashedPassword(username, callback) {
        username = username.trim();
        this.con.query(
            'SELECT password FROM nlogin WHERE last_name = ? LIMIT 1',
            [username.toLowerCase()],
            (err, result) => {
                if (err) throw err;
                callback(result[0] ? result[0].password : "")
            }
        )
    }

    checkPassword(username, password, callback) {
        this.getHashedPassword(username, (hash) => {
            if (hash) {
                var algorithm = this.detectAlgorithm(hash);
                if (algorithm) {
                    callback(algorithm.isValid(password, hash))
                } else {
                    callback(false);
                }
            } else {
                callback(false);
            }
        });
    }

    /**
     * Retorna o algoritmo usado na senha.
     *
     * @param {string} hashed_pass Senha criptografada.
     * @return {any} Retorna o algoritmo usado. Se for desconhecido ou não suportado, retorna null.
     */
    detectAlgorithm(hashed_pass) {
        var algo = (hashed_pass.includes("$") ? hashed_pass.split("$")[1] : '').toUpperCase();
        switch (algo) {
            case "2":
            case "2A":
            case "2Y":
            case "2B":
                return this.bcrypt;

            case "PBKDF2":
                // will be added
                return null;

            case "ARGON2I":
            case "ARGON2ID":
                return this.argon2;
            
            case "SHA256":
                return this.sha256;

            case "SHA":
                return this.authme;

            default:
                return null;
        }
    }

    hash(passwd) {
        return this.def_algo.hash(passwd);
    }

    destruct() {
        if (this.con) {
            this.con.destroy();
            this.con = null;
        }
    }

    getEmail(username, callback) {
        const cleanUsername = username.trim().toLowerCase();
        this.con.query(
            'select email from nlogin where last_name = ? limit 1',
            [cleanUsername],
            (err, result, fields) => {
                if (err) throw err;
                callback(result[0] ? result[0].email : null)
            }
        )
    }

    setEmail(username, email, callback = null) {
        const cleanUsername = username.trim().toLowerCase();
        this.con.query(
            'UPDATE nlogin SET email = ? WHERE last_name = ?',
            [email, cleanUsername],
            (err, result, fields) => {
                if (callback) callback(err == null)
            }
        )
    }

    setIp(username, ip, callback = null) {
        const cleanUsername = username.trim().toLowerCase();
        this.con.query(
            'UPDATE nlogin SET last_ip = ? WHERE last_name = ?',
            [ip, cleanUsername],
            (err, result, fields) => {
                if (callback) callback(err == null)
            }
        )
    }

    getIp(username, callback) {
        const cleanUsername = username.trim().toLowerCase();
        this.con.query(
            'select last_ip from nlogin where last_name = ? limit 1',
            [cleanUsername],
            (err, result, fields) => {
                if (err) throw err;
                callback(result[0] ? result[0].last_ip : null);
            }
        )
    }

    isUserRegistered(username, callback) {
        const cleanUsername = username.trim().toLowerCase();
        this.con.query(
            `SELECT 1 FROM ${TABLE_NAME} WHERE last_name = ? LIMIT 1`,
            [cleanUsername],
            (err, result, fields) => {
                if (err) throw err;
                callback(result.length > 0);
            }
        );
    }

    isIpRegistered(address, callback) {
        this.con.query(
            `SELECT 1 FROM ${TABLE_NAME} WHERE last_ip = ? LIMIT 1`,
            [address],
            (err, result, fields) => {
                if (err) throw err;
                callback(result.length > 0);
            }
        );
    }

    /**
     * Changes password for player.
     *
     * @param {string} username the username
     * @param {string} password the password
     * @return {bool} true whether or not password change was successful
     */
    async changePassword(passwd, username, callback = null) {
        const cleanUsername = username.trim().toLowerCase();
        var hash = await this.hash(passwd);
        this.con.query(
            `UPDATE ${TABLE_NAME} SET password = ? WHERE last_name = ?`,
            [hash, cleanUsername],
            (err, result, fields) => {
                if (callback) callback(err == null)
            }
        )
    }

    getInfo(username, callback) {
        const cleanUsername = username.trim().toLowerCase();
        this.con.query(
            `select * from ${TABLE_NAME} where last_name = ? limit 1`,
            [cleanUsername],
            (err, result, fields) => {
                if (err) throw err;
                callback(result[0]);
            }
        )
    }

    getUUID(username, callback) {
        const cleanUsername = username.trim().toLowerCase();
        this.con.query(
            `SELECT unique_id FROM ${TABLE_NAME} WHERE last_name = ? LIMIT 1`,
            [cleanUsername],
            (err, result, fields) => {
                if (err) throw err;
                let id = result[0] ? result[0].unique_id : null;
                if (id) {
                    // Insert dashes: 8-4-4-4-12
                    id = id.replace(/^(.{8})(.{4})(.{4})(.{4})(.{12})$/, '$1-$2-$3-$4-$5');
                }
                callback(id);
            }
        );
    }
}
module.exports = nLogin;
