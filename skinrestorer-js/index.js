const mysql = require('mysql');
const TABLE_PREFIX = 'sr_';

class Skinrestorer {
    constructor(
        hostname,
        username,
        password,
        database,
    ) {
        this.con = mysql.createConnection({
            host: hostname,
            user: username,
            password: password,
            database: database,
        });

        this.con.connect((err) => {
            if (err) throw err;
            // console.log("Connected to Skinrestorer database!");
        });
    }

    async getName(uuid) {
        return new Promise((resolve, reject) => {
            this.con.query(
                `SELECT name FROM ${TABLE_PREFIX}cache WHERE uuid = ? LIMIT 1`,
                [uuid],
                (err, result) => {
                    if (err) {
                        console.error("Error fetching name:", err);
                        reject(err);
                    } 
                    else resolve(result[0] ? result[0].name : null);
                }
            );
        });
    }

    /**
     * Returns the Microsoft skin if a username matches a Premium account.
     * 
     * @param {uuid} uuid Player's UUID
     * @returns Json string of the skin data
     * @throws {Error} If the skin is not found
     */
    async getDefaultPlayerSkin(uuid) {
        return new Promise((resolve, reject) => {
            this.con.query(
                `SELECT value FROM ${TABLE_PREFIX}player_skins WHERE uuid = ? LIMIT 1`,
                [uuid],
                (err, result) => {
                    if (err) {
                        console.error("Error fetching player skin:", err);
                        reject(err);
                    } 
                    else {
                        const base64 = result[0] ? result[0].value : null;
                        const skinData = base64 ? Buffer.from(base64, 'base64').toString('utf-8') : null;

                        if (skinData == null) {
                            console.error("Skin not found for UUID:", uuid);
                            reject(new Error("Skin not found for UUID: " + uuid));
                        }

                        resolve(skinData);
                    }
                }
            )
        });
    }

    /**
     * Get the skin the player is actually using.
     * 
     * @param {uuid} uuid Player's UUID
     * @returns Json string of the skin data
     * @throws {Error} If the skin is not found
     */
    async getPlayerSkin(uuid) {
        return new Promise((resolve, reject) => {
            this.con.query(
                `SELECT skin_identifier, skin_type FROM ${TABLE_PREFIX}players WHERE uuid = ? LIMIT 1`,
                [uuid],
                (err, result) => {
                    if (err) {
                        console.error("Error fetching player skin:", err);
                        reject(err);
                    } 
                    else {
                        const skinIdentifier = result[0] ? result[0].skin_identifier : null;
                        const skinType = result[0] ? result[0].skin_type : null;

                        if (skinIdentifier == null || skinType == null) {
                            console.error("Skin not found for UUID:", uuid);
                            reject(new Error("Skin not found for UUID: " + uuid));
                        }

                        if (skinType == "PLAYER") {
                            this.getDefaultPlayerSkin(skinIdentifier)
                                .then(resolve)
                                .catch(reject);
                            return;
                        }

                        if (skinType != "URL") {
                            console.error("Skin type not supported:", skinType);
                            reject(new Error("Skin type not supported: " + skinType));
                        }

                        this.con.query(
                            `SELECT value from ${TABLE_PREFIX}url_skins WHERE url = ? LIMIT 1`,
                            [skinIdentifier],
                            (err, result) => {
                                if (err) {
                                    console.error("Error fetching player skin:", err);
                                    reject(err);
                                } 
                                else {
                                    const base64 = result[0] ? result[0].value : null;
                                    const skinData = base64 ? Buffer.from(base64, 'base64').toString('utf-8') : null;

                                    if (skinData == null) {
                                        console.error("Skin not found for UUID:", uuid);
                                        reject(new Error("Skin not found for UUID: " + uuid));
                                    }

                                    resolve(skinData);
                                }
                            }
                        )
                    }
                }
            )
        });
    } 

    extractSkinURL(skinData) {
        const skinDataObj = JSON.parse(skinData);
        const skinURL = skinDataObj.textures.SKIN.url;
        return skinURL;
    }

    extractCapeURL(skinData) {
        const skinDataObj = JSON.parse(skinData);
        const capeURL = skinDataObj.textures.CAPE.url;
        return capeURL;
    }
}

module.exports = Skinrestorer;