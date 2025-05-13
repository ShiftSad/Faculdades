import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { StatisticEvent, StatisticEventSchema } from './schemas/statistic-event.schema';
import { StatisticsService } from './statistics.service';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: StatisticEvent.name, schema: StatisticEventSchema }
    ]),
  ],
  providers: [StatisticsService],
  exports: [StatisticsService],
})
export class StatisticsModule {}