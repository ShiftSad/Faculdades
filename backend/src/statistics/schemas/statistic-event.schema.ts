import { Prop, Schema, SchemaFactory } from "@nestjs/mongoose";
import { Document } from "mongoose"; 

@Schema({ timestamps: true })
export class StatisticEvent extends Document {
    @Prop({ required: true })
    player_uuid: string;

    @Prop({ required: true })
    player_name: string;

    @Prop({ required: true })
    event_type: string;

    @Prop({ type: Object, required: true })	
    event_data: Record<string, any>;
}

export const StatisticEventSchema = SchemaFactory.createForClass(StatisticEvent);

StatisticEventSchema.index({ player_uuid: 1, player_name: 1, event_type: 1, createdAt: 1 }, { unique: true });