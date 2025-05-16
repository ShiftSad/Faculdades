import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { PlayerStatsSnapshotSchema, StatisticEventSnapshot } from './schemas/statistic-event.schema';
import { StatisticsService } from './statistics.service';
import { StatisticsController } from './statistics/statistics.controller';
import { NloginService } from 'src/auth/nlogin/nlogin.service';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: StatisticEventSnapshot.name, schema: PlayerStatsSnapshotSchema }
    ]),
  ],
  providers: [
    StatisticsService,
    NloginService
  ],
  exports: [StatisticsService],
  controllers: [StatisticsController],
})
export class StatisticsModule {}