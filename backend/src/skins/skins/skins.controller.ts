import { Controller, Get, Param } from '@nestjs/common';
import { SkinrestorerService } from '../skinrestorer/skinrestorer.service';

@Controller('skins')
export class SkinsController {
    constructor(
        private readonly srService: SkinrestorerService
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
    async getPlayerSkin(
        @Param('identifier') identifier: string
    ): Promise<string | null> {
        const uuid = await this.resolveUUID(identifier);
        if (!uuid) return null;
        try {
            const skin = await this.srService.getPlayerSkin(uuid);
            return skin || null;
        } catch (err) {
            console.error('Error fetching player skin:', err);
            return null;
        }
    }

    @Get('skin/:identifier')
    async getSkinURL(
        @Param('identifier') identifier: string
    ): Promise<string | null> {
        const uuid = await this.resolveUUID(identifier);
        if (!uuid) return null;
        try {
            const skin = await this.srService.getPlayerSkin(uuid);
            if (!skin) return null;

            const skinURL = this.srService.extractSkinURL(skin);
            return skinURL || null;
        } catch (err) {
            console.error('Error fetching skin URL:', err);
            return null;
        }
    }

    @Get('cape/:identifier')
    async getCapeURL(
        @Param('identifier') identifier: string
    ): Promise<string | null> {
        const uuid = await this.resolveUUID(identifier);
        if (!uuid) return null;
        try {
            const skin = await this.srService.getPlayerSkin(uuid);
            if (!skin) return null;

            const capeURL = this.srService.extractCapeURL(skin);
            return capeURL || null;
        } catch (err) {
            console.error('Error fetching cape URL:', err);
            return null;
        }
    }
}
