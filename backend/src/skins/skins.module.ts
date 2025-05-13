import { Module } from '@nestjs/common';
import { SkinsController } from './skins/skins.controller';
import { SkinrestorerService } from './skinrestorer/skinrestorer.service';

@Module({
  controllers: [SkinsController],
  providers: [SkinrestorerService]
})
export class SkinsModule {}
