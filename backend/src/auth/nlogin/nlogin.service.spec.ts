import { Test, TestingModule } from '@nestjs/testing';
import { NloginService } from './nlogin.service';

describe('NloginService', () => {
  let service: NloginService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [NloginService],
    }).compile();

    service = module.get<NloginService>(NloginService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
