import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { PlayerStatsSnapshotSchema, StatisticEventSnapshot } from './schemas/statistic-event.schema';
import { StatisticsService } from './statistics.service';
import { StatisticsController } from './statistics/statistics.controller';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: StatisticEventSnapshot.name, schema: PlayerStatsSnapshotSchema }
    ]),
  ],
  providers: [StatisticsService],
  exports: [StatisticsService],
  controllers: [StatisticsController],
})
export class StatisticsModule {}