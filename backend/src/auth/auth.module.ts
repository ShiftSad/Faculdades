import { Module } from '@nestjs/common';
import { NloginService } from './nlogin/nlogin.service';
import { AuthController } from './auth/auth.controller';
import { ConfigModule } from '@nestjs/config';

@Module({
  imports: [ConfigModule],
  providers: [NloginService],
  controllers: [AuthController],
  exports: [NloginService],
})
export class AuthModule {}
