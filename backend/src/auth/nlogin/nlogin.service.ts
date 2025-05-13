import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

const Nlogin = require('nlogin');

@Injectable()
export class NloginService implements OnModuleInit {
    private readonly logger = new Logger(NloginService.name);
    private nloginInstance: any;
    private isConnected = false;

    constructor(private config: ConfigService) { } 

    async onModuleInit() {
        const host = this.config.get<string>('NLOGIN_DB_HOST');
        const user = this.config.get<string>('NLOGIN_DB_USER');
        const password = this.config.get<string>('NLOGIN_DB_PASSWORD');
        const database = this.config.get<string>('NLOGIN_DB_DATABASE');

        if (!host || !user || !password || !database) {
            this.logger.error('Nlogin configuration is missing');
            return;
        }

        try {
            this.nloginInstance = new Nlogin(host, user, password, database, (err: any) => {
                if (err) {
                    this.logger.error('Error connecting to Nlogin:', err);
                } else {
                    this.isConnected = true;
                    this.logger.log('Connected to Nlogin database');
                }
            })

            await new Promise(resolve => setTimeout(resolve, 2000)); // Wait for connection to establish

            if (!this.isConnected) {
                this.logger.error('Failed to connect to Nlogin database');
            } else {
                this.logger.log('Nlogin service initialized successfully');
            }
        } catch (error) {
            this.logger.error('Error initializing Nlogin service:', error);
            this.isConnected = false;
        }
    }

    checkPassword(username: string, password: string): Promise<boolean> {
        return new Promise((resolve, reject) => {
            if (!this.isConnected) {
                this.logger.error('Nlogin service is not connected');
                return reject(new Error('Nlogin service is not connected'));
            }

            this.nloginInstance.checkPassword(username, password, (isCorrectPass: boolean) => {
                this.logger.debug(`checkPassword result for user ${username}: ${isCorrectPass}`);
                resolve(isCorrectPass);
            });
        });
    }
}
