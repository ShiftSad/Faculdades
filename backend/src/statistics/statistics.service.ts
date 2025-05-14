import { Injectable, Logger } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { StatisticEventSnapshot } from './schemas/statistic-event.schema';
import { Model } from 'mongoose';
import { StatisticEventSnapshotDto } from './dto/statistic-event.dto';

@Injectable()
export class StatisticsService {
    private readonly logger = new Logger(StatisticsService.name);

    constructor(
        @InjectModel(StatisticEventSnapshot.name) private statisticEventModel: Model<StatisticEventSnapshot>,
    ) {}

    async ingest(events: StatisticEventSnapshotDto): Promise<void> {
        const statisticEvent = new this.statisticEventModel({
            playerId: events.playerId,
            timestamp: new Date(), // Assure timestamp is set to the current date
            stats: events.stats,
        });

        try {
            await statisticEvent.save();
            this.logger.log('Statistic event saved successfully');
        } catch (error) {
            this.logger.error('Error saving statistic event', error);
        }
    }
}
