import { async, TestBed } from '@angular/core/testing';
import { SchulkatalogApiModule } from './schulkatalog-api.module';

describe('SchulkatalogApiModule', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [SchulkatalogApiModule]
    }).compileComponents();
  }));

  it('should create', () => {
    expect(SchulkatalogApiModule).toBeDefined();
  });
});
