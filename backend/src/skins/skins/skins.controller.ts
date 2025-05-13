import { Controller, Get, Param } from '@nestjs/common';
import { SkinrestorerService } from '../skinrestorer/skinrestorer.service';

@Controller('skins')
export class SkinsController {
    constructor(
        private readonly srService: SkinrestorerService,
    ) { }

    private async resolveUUID(identifier: string): Promise<string | null> {
        const uuidRegex =
            /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
        if (uuidRegex.test(identifier)) {
            return identifier;
        }
        try {
            const uuid = await this.srService.getUUID(identifier);
            return uuid || null;
        } catch (err) {
            console.error('Error resolving UUID:', err);
            return null;
        }
    }

    @Get('skindata/:identifier')
    async getPlayerSkin(@Param('identifier') identifier: string) {
        const uuid = await this.resolveUUID(identifier);
        if (!uuid) {
            return {
                success: false,
                message: "Invalid identifier or UUID not found"
            };
        }
        try {
            const skin = await this.srService.getPlayerSkin(uuid);
            if (!skin) 
                return {
                    success: false,
                    message: "No skin found for this player"
                };
            
            return {
                success: true,
                message: "Skin found",
                data: JSON.parse(skin)
            }
        } catch (err) {
            return {
                success: false,
                message: "Error fetching skin data"
            }
        }
    }

    @Get('skin/:identifier')
    async getSkinURL(@Param('identifier') identifier: string) {
        const uuid = await this.resolveUUID(identifier);
        if (!uuid) {
            return {
                success: false,
                message: "Invalid identifier or UUID not found"
            };
        }
        try {
            const skin = await this.srService.getPlayerSkin(uuid);
            if (!skin) {
                return {
                    success: false,
                    message: "No skin found for this player"
                };
            }

            const skinURL = this.srService.extractSkinURL(skin);
            if (!skinURL) {
                return {
                    success: false,
                    message: "No skin URL found"
                };
            }

            return {
                success: true,
                message: "Skin URL found",
                data: skinURL
            };
        } catch (err) {
            return {
                success: false,
                message: "Error fetching skin URL"
            };
        }
    }

    @Get('cape/:identifier')
    async getCapeURL(@Param('identifier') identifier: string) {
        const uuid = await this.resolveUUID(identifier);
        if (!uuid) {
            return {
                success: false,
                message: "Invalid identifier or UUID not found"
            };
        }
        try {
            const skin = await this.srService.getPlayerSkin(uuid);
            if (!skin) {
                return {
                    success: false,
                    message: "No skin found for this player"
                };
            }

            const capeURL = this.srService.extractCapeURL(skin);
            if (!capeURL) {
                return {
                    success: false,
                    message: "No cape URL found"
                };
            }

            return {
                success: true,
                message: "Cape URL found",
                data: capeURL
            };
        } catch (err) {
            return {
                success: false,
                message: "Error fetching cape URL"
            };
        }
    }
}
