import { Module } from '@nestjs/common';
import { NloginService } from './nlogin/nlogin.service';
import { AuthController } from './auth/auth.controller';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { JwtModule } from '@nestjs/jwt';

@Module({
  imports: [
    ConfigModule,
    JwtModule.registerAsync({
      imports: [ConfigModule],
      useFactory: (config: ConfigService) => ({
        secret: config.get<string>('JWT_SECRET'),
        signOptions: { expiresIn: '60m' }, // 60 minutes validity
      }),
      inject: [ConfigService],
    }),
  ],
  providers: [NloginService],
  controllers: [AuthController],
  exports: [NloginService],
})
export class AuthModule {}
