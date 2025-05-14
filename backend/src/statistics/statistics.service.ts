import { Injectable, Logger } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { StatisticEvent, StatisticEventSnapshot } from './schemas/statistic-event.schema';
import { Model } from 'mongoose';
import { StatisticEventSnapshotDto } from './dto/statistic-event.dto';
import { GraphableStatisticName } from './dto/graphable-statistic.dto';

@Injectable()
export class StatisticsService {
    private readonly logger = new Logger(StatisticsService.name);

    constructor(
        @InjectModel(StatisticEventSnapshot.name) private statisticEventModel: Model<StatisticEventSnapshot>,
    ) {}

    async ingest(events: StatisticEventSnapshotDto): Promise<void> {
        const statisticEvent = new this.statisticEventModel({
            playerId: events.playerId,
            timestamp: new Date(),
            stats: events.stats,
        });

        try {
            await statisticEvent.save();
            this.logger.log('Statistic event saved successfully');
        } catch (error) {
            this.logger.error('Error saving statistic event', error);
        }
    }

    async findAll(): Promise<StatisticEventSnapshot[]> {
        this.logger.log('Fetching all statistic events');
        return this.statisticEventModel.find().sort({ timestamp: 'desc' }).exec();
    }

    async findByPlayerId(playerId: string): Promise<StatisticEventSnapshot[]> {
        this.logger.log(`Fetching statistic events for player ID: ${playerId}`);
        const events = await this.statisticEventModel.find({ playerId }).sort({ timestamp: 'desc' }).exec();
        return events;
    }

    async findLatestByPlayerId(playerId: string): Promise<StatisticEventSnapshot | null> {
        this.logger.log(`Fetching latest statistic event for player ID: ${playerId}`);
        const event = await this.statisticEventModel
            .findOne({ playerId })
            .sort({ timestamp: 'desc' })
            .exec();
        return event;
    }

    async getStatisticHistoryForPlayer(
        playerId: string,
        statisticName: GraphableStatisticName,
        startDate: Date,
        endDate: Date,
    ): Promise<{ timestamp: Date; value: number | undefined }[]> {
        this.logger.log(
            `Fetching history for statistic '${statisticName}' for player ID '${playerId}' from ${startDate.toISOString()} to ${endDate.toISOString()}`,
        );

        const snapshots = await this.statisticEventModel.find({
            playerId,
            timestamp: { $gte: startDate, $lte: endDate },
        }).select({ timestamp: 1, stats: 1, _id: 0 })
          .sort({ timestamp: 'asc' })
          .lean()
          .exec();

        if (!snapshots || snapshots.length === 0) {
            this.logger.warn(`No snapshots found for player '${playerId}' and statistic '${statisticName}' in the given date range.`);
            return [];
        }
        
        return snapshots.map(snapshot => {
            const value = (snapshot.stats as StatisticEvent)[statisticName as keyof StatisticEvent];
            return {
                timestamp: snapshot.timestamp,
                value: typeof value === 'number' ? value : undefined,
            };
        });
    }
}
