import { BadRequestException, Body, Controller, Get, Headers, Logger, NotFoundException, Param, ParseEnumPipe, Post, Query, UnauthorizedException, UsePipes, ValidationPipe } from '@nestjs/common';
import { StatisticsService } from '../statistics.service';
import { StatisticEventSnapshotDto } from '../dto/statistic-event.dto';
import { ConfigService } from '@nestjs/config';
import { StatisticEventSnapshot } from '../schemas/statistic-event.schema';
import { GraphableStatisticName, StatisticHistoryQueryDto } from '../dto/graphable-statistic.dto';
import { NloginService } from 'src/auth/nlogin/nlogin.service';

@Controller('statistics')
export class StatisticsController {

    constructor(
        private readonly statisticsService: StatisticsService,
        private readonly nloginService: NloginService,
        private readonly configService: ConfigService,
    ) {}

    @Post('ingest')
    async ingestStatistics(
        @Headers('authorization') authHeader: string,
        @Body() events: StatisticEventSnapshotDto,
    ) {
        const apiToken = this.configService.get<string>('API_TOKEN');
        const expectedHeader = `Bearer ${apiToken}`;

        if (!authHeader || authHeader !== expectedHeader) {
            throw new UnauthorizedException('Invalid or missing API token');
        }

        await this.statisticsService.ingest(events);
        return {
            success: true,
            message: "Statistics ingested successfully"
        };
    }

    @Get('all')
    async getAllStatistics(): Promise<StatisticEventSnapshot[]> {
        return this.statisticsService.findAll();
    }

    @Get('player/:playerId')
    async getStatisticsByPlayerId(@Param('playerId') playerId: string): Promise<StatisticEventSnapshot[]> {
        const uuid = await this.resolvePlayerUUID(playerId);
        const events = await this.statisticsService.findByPlayerId(uuid);
        if (!events || events.length === 0) {
            throw new NotFoundException(`No statistics found for player ID ${uuid}`);
        }
        return events;
    }

    @Get('player/:playerId/latest')
    async getLatestStatisticsByPlayerId(@Param('playerId') playerId: string): Promise<StatisticEventSnapshot> {
        const uuid = await this.resolvePlayerUUID(playerId);
        const event = await this.statisticsService.findLatestByPlayerId(uuid);
        if (!event) {
            throw new NotFoundException(`No latest statistics found for player ID ${uuid}`);
        }
        return event;
    }

    @Get('player/:playerId/history/:statisticName')
    @UsePipes(new ValidationPipe({ transform: true, whitelist: true, forbidNonWhitelisted: true }))
    async getPlayerStatisticHistory(
        @Param('playerId') playerId: string,
        @Param('statisticName', new ParseEnumPipe(GraphableStatisticName)) statisticName: GraphableStatisticName,
        @Query() query: StatisticHistoryQueryDto,
    ): Promise<{ timestamp: Date; value: number | undefined }[]> {
        const uuid = await this.resolvePlayerUUID(playerId);
        const { startDate, endDate } = query;
        if (startDate >= endDate) {
            throw new BadRequestException('End date must be after start date.');
        }
        const historyData = await this.statisticsService.getStatisticHistoryForPlayer(uuid, statisticName, startDate, endDate);
        return historyData;
    }

    private isUUID(uuid: string): boolean {
        const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
        return uuidRegex.test(uuid);
    }

    private async resolvePlayerUUID(identifier: string): Promise<string> {
        if (this.isUUID(identifier)) {
            return identifier;
        }
        const uuid = await this.nloginService.getUUID(identifier);
        if (!uuid) {
            throw new NotFoundException(`Player not found with identifier ${identifier}`);
        }
        return uuid;
    }
}
