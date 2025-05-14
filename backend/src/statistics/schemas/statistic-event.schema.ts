import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

export interface CropsHarvested {
	wheat?: number;
	carrots?: number;
	potatoes?: number;
	beetroot?: number;
	netherWart?: number;
	melon?: number;
	pumpkin?: number;
}

export interface StatisticEvent {
	timePlayedSeconds?: number;
	blocksMined?: number;
	playersKilled?: number;
	deaths?: number;
	itemsCrafted?: number;
	fishCaught?: number;
	animalsBred?: number;
	mobKills?: number;
	walkOneCm?: number;
	jump?: number;
	sprintOneCm?: number;
	crouchOneCm?: number;
	fallOneCm?: number;
	swimOneCm?: number;
	flyOneCm?: number;
	climbOneCm?: number;
	useItem?: number;
	breakItem?: number;
	talkedToVillager?: number;
	tradedWithVillager?: number;
	cropsHarvested?: CropsHarvested;
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

export const PlayerStatsSnapshotSchema = SchemaFactory.createForClass(
	StatisticEventSnapshot,
);

PlayerStatsSnapshotSchema.index({ playerId: 1, timestamp: 1 });
