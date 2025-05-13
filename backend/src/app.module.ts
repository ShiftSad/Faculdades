import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { StatisticsService } from './statistics/statistics.service';
import { MongooseModule } from '@nestjs/mongoose';
import { StatisticsModule } from './statistics/statistics.module';
import { SkinsModule } from './skins/skins.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true
    }),
    MongooseModule.forRootAsync({
      imports: [ConfigModule],
      inject: [ConfigService],
      useFactory: (config: ConfigService) => ({
        uri: config.get<string>('MONGODB_URI'),
        dbName: config.get<string>('MONGODB_DB') || 'minecraft',
      }),
    }),
    AuthModule,
    StatisticsModule,
    SkinsModule
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
