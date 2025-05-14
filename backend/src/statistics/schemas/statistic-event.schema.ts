import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

export interface StatisticEvent {
    timePlayedSeconds?: number;
    blocksMined?: number;
    playersKilled?: number;
    deaths?: number;
    itemsCrafted?: number;
    distanceTraveledBlocks?: number;
    cropsHarvested?: CropsHarvested;
}

export interface CropsHarvested {
    wheat?: number;
    carrots?: number;
    potatoes?: number;
    beetroot?: number;
    netherWart?: number;
    melon?: number;
    pumpkin?: number;
}

@Schema({
    timestamps: false,
    collection: 'playerStatsSnapshots',
})
export class StatisticEventSnapshot extends Document {
    @Prop({ required: true, index: true })
    playerId: string;

    @Prop({ required: true })
    timestamp: Date;

    @Prop({ type: Object })
    stats: StatisticEvent;
}

export const PlayerStatsSnapshotSchema = SchemaFactory.createForClass(StatisticEventSnapshot);

PlayerStatsSnapshotSchema.index({ playerId: 1, timestamp: 1 });
