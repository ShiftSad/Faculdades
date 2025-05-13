const mysql = require('mysql');
const TABLE_PREFIX = 'sr_';

// Load file hardcoded_skins.json
const fs = require('fs');
const hardcodedSkins = JSON.parse(fs.readFileSync(__dirname + '/hardcoded_skins.json', 'utf8'));

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
            if (err) {
                console.error("Database connection error:", err);
                throw err;
            }
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
                        // Keep database query errors
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
     * @param {string} uuid Player's UUID
     * @returns Json string of the skin data or null if not found
     */
    async getDefaultPlayerSkin(uuid) {
        return new Promise((resolve, reject) => {
            this.con.query(
                `SELECT value FROM ${TABLE_PREFIX}player_skins WHERE uuid = ? LIMIT 1`,
                [uuid],
                (err, result) => {
                    if (err) {
                        console.error("Error fetching default player skin:", err);
                        reject(err);
                    } 
                    else {
                        const base64 = result[0] ? result[0].value : null;
                        const skinData = base64 ? Buffer.from(base64, 'base64').toString('utf-8') : null;

                        resolve(skinData);
                    }
                }
            )
        });
    }

    /**
     * Get the skin the player is actually using.
     * 
     * @param {string} uuid Player's UUID
     * @returns Json string of the skin data or null if not found or unsupported type
     */
    async getPlayerSkin(uuid) {
        return new Promise((resolve, reject) => {
            this.con.query(
                `SELECT skin_identifier, skin_type FROM ${TABLE_PREFIX}players WHERE uuid = ? LIMIT 1`,
                [uuid],
                (err, result) => {
                    if (err) {
                        // Keep database query errors
                        console.error("Error fetching player skin entry:", err);
                        reject(err);
                    } 
                    else {
                        const skinIdentifier = result[0] ? result[0].skin_identifier : null;
                        const skinType = result[0] ? result[0].skin_type : null;

                        // Resolve with null if no skin entry is found
                        if (skinIdentifier == null || skinType == null) {
                            resolve(null);
                            return;
                        }

                        if (skinType === "PLAYER") {
                            this.getDefaultPlayerSkin(skinIdentifier)
                                .then(resolve)
                                .catch(reject); 
                            return;
                        }

                        if (skinType == "URL") {
                            // Handle URL skin type
                            this.con.query(
                                `SELECT value from ${TABLE_PREFIX}url_skins WHERE url = ? LIMIT 1`,
                                [skinIdentifier],
                                (err, result) => {
                                    if (err) {
                                        console.error("Error fetching URL skin data:", err);
                                        reject(err);
                                    } 
                                    else {
                                        const base64 = result[0] ? result[0].value : null;
                                        const skinData = base64 ? Buffer.from(base64, 'base64').toString('utf-8') : null;

                                        resolve(skinData);
                                    }
                                }
                            );
                        }

                        if (skinType == "CUSTOM") {
                            const skinDataBase64 = hardcodedSkins[skinIdentifier];
                            if (skinDataBase64) {
                                const skinValue = hardcodedSkins[skinIdentifier].value;
                                if (!skinValue) {
                                    console.error("Hardcoded skin value not found for identifier:", skinIdentifier);
                                    resolve(null);
                                    return;
                                }

                                const skinData = Buffer.from(skinValue, 'base64').toString('utf-8');
                                
                                resolve(skinData);
                            } else {
                                // Handle case where skin data is not found
                                console.error("Hardcoded skin not found for identifier:", skinIdentifier);
                                resolve(null);
                            }
                        }
                    }
                }
            );
        });
    } 

    /**
     * Extracts the skin URL from the skin data JSON string.
     * @param {string} skinData JSON string of skin data. Can be null.
     * @returns Skin URL string or null if data is null or URL is not found.
     */
    extractSkinURL(skinData) {
        if (!skinData) return null;
        try {
            const skinDataObj = JSON.parse(skinData);
            return skinDataObj?.textures?.SKIN?.url || null;
        } catch (e) {
            return null;
        }
    }

     /**
     * Extracts the cape URL from the skin data JSON string.
     * @param {string} skinData JSON string of skin data. Can be null.
     * @returns Cape URL string or null if data is null or URL is not found.
     */
    extractCapeURL(skinData) {
         if (!skinData) return null;
        try {
            const skinDataObj = JSON.parse(skinData);
            return skinDataObj?.textures?.CAPE?.url || null;
        } catch (e) {
            return null;
        }
    }
}

module.exports = Skinrestorer;