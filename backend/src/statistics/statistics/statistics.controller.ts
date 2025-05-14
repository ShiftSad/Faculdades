import { Body, Controller, Post } from '@nestjs/common';
import { StatisticsService } from '../statistics.service';
import { StatisticEventSnapshotDto } from '../dto/statistic-event.dto';

@Controller('statistics')
export class StatisticsController {

    constructor(
        private readonly statisticsService: StatisticsService,
    ) { }

    @Post('ingest')
    async ingestStatistics(@Body() events: StatisticEventSnapshotDto) {
        try {
            await this.statisticsService.ingest(events);
            return {
                success: true,
                message: "Statistics ingested successfully"
            };
        } catch (error) {
            return {
                success: false,
                message: "Error ingesting statistics",
                error: error.message
            };
        }
    }
}
