import {
	IsString,
	IsNumber,
	IsDate,
	IsOptional,
	ValidateNested,
	IsNotEmpty,
} from 'class-validator';
import { Type } from 'class-transformer';

export class CropsHarvestedDto {
	@IsNumber()
	@IsOptional()
	wheat?: number;

	@IsNumber()
	@IsOptional()
	carrots?: number;

	@IsNumber()
	@IsOptional()
	potatoes?: number;

	@IsNumber()
	@IsOptional()
	beetroot?: number;

	@IsNumber()
	@IsOptional()
	netherWart?: number;

	@IsNumber()
	@IsOptional()
	melon?: number;

	@IsNumber()
	@IsOptional()
	pumpkin?: number;
}

export class StatisticEventDto {
	@IsNumber()
	@IsOptional()
	timePlayedSeconds?: number;

	@IsNumber()
	@IsOptional()
	blocksMined?: number;

	@IsNumber()
	@IsOptional()
	playersKilled?: number;

	@IsNumber()
	@IsOptional()
	deaths?: number;

	@IsNumber()
	@IsOptional()
	itemsCrafted?: number;

	@IsNumber()
	@IsOptional()
	distanceTraveledBlocks?: number;

	@IsNumber()
	@IsOptional()
	fishCaught?: number;

	@IsNumber()
	@IsOptional()
	animalsBred?: number;

	@IsNumber()
	@IsOptional()
	mobKills?: number;

	@IsNumber()
	@IsOptional()
	walkOneCm?: number;

	@IsNumber()
	@IsOptional()
	jump?: number;

	@IsNumber()
	@IsOptional()
	sprintOneCm?: number;

	@IsNumber()
	@IsOptional()
	crouchOneCm?: number;

	@IsNumber()
	@IsOptional()
	fallOneCm?: number;

	@IsNumber()
	@IsOptional()
	swimOneCm?: number;

	@IsNumber()
	@IsOptional()
	flyOneCm?: number;

	@IsNumber()
	@IsOptional()
	climbOneCm?: number;

	@IsNumber()
	@IsOptional()
	useItem?: number;

	@IsNumber()
	@IsOptional()
	breakItem?: number;

	@IsNumber()
	@IsOptional()
	talkedToVillager?: number;

	@IsNumber()
	@IsOptional()
	tradedWithVillager?: number;

	@ValidateNested()
	@Type(() => CropsHarvestedDto)
	@IsOptional()
	cropsHarvested?: CropsHarvestedDto;
}

export class StatisticEventSnapshotDto {
	@IsString()
	@IsNotEmpty()
	playerId: string;

	@IsDate()
	@Type(() => Date)
	@IsNotEmpty()
	timestamp: Date;

	@ValidateNested()
	@Type(() => StatisticEventDto)
	@IsNotEmpty()
	stats: StatisticEventDto;
}
