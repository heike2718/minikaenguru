import { async, TestBed } from '@angular/core/testing';
import { CommonLoggingModule } from './common-logging.module';

describe('CommonLoggingModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [CommonLoggingModule]
    }).compileComponents();
  }));

  it('should create', () => {
    expect(CommonLoggingModule).toBeDefined();
  });
});
