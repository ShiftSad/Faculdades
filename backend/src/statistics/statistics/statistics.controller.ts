import { Body, Controller, Headers, Post, UnauthorizedException } from '@nestjs/common';
import { StatisticsService } from '../statistics.service';
import { StatisticEventSnapshotDto } from '../dto/statistic-event.dto';
import { ConfigService } from '@nestjs/config';

@Controller('statistics')
export class StatisticsController {

    constructor(
        private readonly statisticsService: StatisticsService,
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

        try {
            await this.statisticsService.ingest(events);
            return {
                success: true,
                message: 'Statistics ingested successfully',
            };
        } catch (error) {
            return {
                success: false,
                message: 'Error ingesting statistics',
                error: error.message,
            };
        }
    }
}
