import { Module } from '@nestjs/common';
import { SkinsController } from './skins/skins.controller';
import { SkinrestorerService } from './skinrestorer/skinrestorer.service';
import { NloginService } from 'src/auth/nlogin/nlogin.service';

@Module({
  controllers: [SkinsController],
  providers: [
    SkinrestorerService,
    NloginService
  ]
})
export class SkinsModule {}
