import { Test, TestingModule } from '@nestjs/testing';
import { SkinrestorerService } from './skinrestorer.service';

describe('SkinrestorerService', () => {
  let service: SkinrestorerService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [SkinrestorerService],
    }).compile();

    service = module.get<SkinrestorerService>(SkinrestorerService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
