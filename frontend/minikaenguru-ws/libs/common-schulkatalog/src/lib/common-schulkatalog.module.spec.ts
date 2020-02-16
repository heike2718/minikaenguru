import { async, TestBed } from '@angular/core/testing';
import { CommonSchulkatalogModule } from './common-schulkatalog.module';

describe('CommonSchulkatalogModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [CommonSchulkatalogModule]
    }).compileComponents();
  }));

  it('should create', () => {
    expect(CommonSchulkatalogModule).toBeDefined();
  });
});
