import { TestBed } from '@angular/core/testing';

import { KatalogService } from "././katalog.service";

describe('KatalogService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: KatalogService = TestBed.get(KatalogService);
    expect(service).toBeTruthy();
  });
});
