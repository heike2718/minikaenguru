import { async, TestBed } from '@angular/core/testing';
import { SchulkatalogDomainModule } from './schulkatalog-domain.module';

describe('SchulkatalogDomainModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [SchulkatalogDomainModule]
    }).compileComponents();
  }));

  it('should create', () => {
    expect(SchulkatalogDomainModule).toBeDefined();
  });
});
