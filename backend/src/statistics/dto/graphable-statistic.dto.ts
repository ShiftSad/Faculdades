import { IsDate, IsEnum, IsNotEmpty } from 'class-validator';
import { Type } from 'class-transformer';

export enum GraphableStatisticName {
    TIME_PLAYED_SECONDS = 'timePlayedSeconds',
    BLOCKS_MINED = 'blocksMined',
    PLAYERS_KILLED = 'playersKilled',
    DEATHS = 'deaths',
    ITEMS_CRAFTED = 'itemsCrafted',
    FISH_CAUGHT = 'fishCaught',
    ANIMALS_BRED = 'animalsBred',
    MOB_KILLS = 'mobKills',
    WALK_ONE_CM = 'walkOneCm',
    JUMP = 'jump',
    SPRINT_ONE_CM = 'sprintOneCm',
    CROUCH_ONE_CM = 'crouchOneCm',
    FALL_ONE_CM = 'fallOneCm',
    SWIM_ONE_CM = 'swimOneCm',
    FLY_ONE_CM = 'flyOneCm',
    CLIMB_ONE_CM = 'climbOneCm',
    USE_ITEM = 'useItem',
    BREAK_ITEM = 'breakItem',
    TALKED_TO_VILLAGER = 'talkedToVillager',
    TRADED_WITH_VILLAGER = 'tradedWithVillager',
}

export class StatisticHistoryQueryDto {
    @IsDate()
    @Type(() => Date)
    @IsNotEmpty()
    startDate: Date;

    @IsDate()
    @Type(() => Date)
    @IsNotEmpty()
    endDate: Date;
}