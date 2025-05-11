import { Injectable, Logger } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { StatisticEvent } from './schemas/statistic-event.schema';
import { Model } from 'mongoose';
import { StatisticEventDto } from './dto/statistic-event.dto';

@Injectable()
export class StatisticsService {
    private readonly logger = new Logger(StatisticsService.name);

    constructor(
        @InjectModel(StatisticEvent.name) private statisticEventModel: Model<StatisticEvent>,
    ) {}

    async ingestBatch(events: StatisticEventDto[]): Promise<void> {
        if (!events || events.length === 0) {
            this.logger.warn('Attempted to ingest an empty batch of statistics.');
            return;
        }

        this.logger.debug(`Ingesting batch of ${events.length} statistics into MongoDB.`);

        try {
            await this.statisticEventModel.insertMany(events);
            this.logger.log(`Successfully ingested ${events.length} statistics batch into MongoDB.`);
        } catch (error) {
            this.logger.error(`Failed to ingest statistics batch: ${error.message}`, error.stack);
        }
    }
}
