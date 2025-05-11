import {
  IsUUID,
  IsString,
  IsNotEmpty,
  IsObject,
} from 'class-validator';

export class StatisticEventDto {
  @IsUUID('4', { message: 'player_uuid must be a valid UUID' })
  @IsNotEmpty({ message: 'player_uuid cannot be empty' })
  player_uuid: string;

  @IsString({ message: 'event_type must be a string' })
  @IsNotEmpty({ message: 'event_type cannot be empty' })
  event_type: string;

  @IsObject({ message: 'event_data must be a JSON object' })
  @IsNotEmpty({ message: 'event_data cannot be empty' }) 
  event_data: Record<string, any>;
}