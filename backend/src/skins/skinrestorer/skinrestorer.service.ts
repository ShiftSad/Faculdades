import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

const Skinrestorer = require('skinrestorer');

@Injectable()
export class SkinrestorerService implements OnModuleInit {
    private readonly logger = new Logger(SkinrestorerService.name);
    private srInstance: any;
    private isConnected = false;

    constructor(private config: ConfigService) { }

    async onModuleInit() {
        const host = this.config.get<string>('SKINRESTORER_DB_HOST');
        const user = this.config.get<string>('SKINRESTORER_DB_USER');
        const password = this.config.get<string>('SKINRESTORER_DB_PASSWORD');
        const database = this.config.get<string>('SKINRESTORER_DB_DATABASE');

        if (!host || !user || !password || !database) {
            this.logger.error('Skinrestorer configuration is missing');
            return;
        }

        try {
            this.srInstance = new Skinrestorer(host, user, password, database);
            this.isConnected = true;
        } catch (error) {
            this.logger.error('Error initializing Skinrestorer service:', error);
            this.isConnected = false;
        }
    }

    async getPlayerSkin(uuid: string): Promise<string | null> {
        if (!this.isConnected) {
            this.logger.error('Skinrestorer service is not connected');
            return null;
        }

        try {
            const skin = await this.srInstance.getPlayerSkin(uuid);
            return skin ?? null;
        } catch (err) {
            this.logger.error('Error fetching player skin:', err);
            return null;
        }
    }

    async getUUID(username: string): Promise<string | null> {
        if (!this.isConnected) {
            this.logger.error('Skinrestorer service is not connected');
            return null;
        }

        const uuid = await this.srInstance.getUUID(username);
        if (!uuid) {
            this.logger.error(`No UUID found for username: ${username}`);
            return null;
        }

        return uuid;
    }

    extractSkinURL(skinData: string): string | null {
        if (!this.isConnected) {
            this.logger.error('Skinrestorer service is not connected');
            return null;
        }

        try {
            const url = this.srInstance.extractSkinURL(skinData);
            return url ?? null;
        } catch (err) {
            this.logger.error('Error extracting URL from skin data:', err);
            return null;
        }
    }

    extractCapeURL(capeData: string): string | null {
        if (!this.isConnected) {
            this.logger.error('Skinrestorer service is not connected');
            return null;
        }

        try {
            const url = this.srInstance.extractCapeURL(capeData);
            return url ?? null;
        } catch (err) {
            this.logger.error('Error extracting URL from cape data:', err);
            return null;
        }
    }
}
