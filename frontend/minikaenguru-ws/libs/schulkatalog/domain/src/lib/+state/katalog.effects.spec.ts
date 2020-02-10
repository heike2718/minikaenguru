import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';

import { KatalogEffects } from './katalog.effects';

describe('KatalogEffects', () => {
  let actions$: Observable<any>;
  let effects: KatalogEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        KatalogEffects,
        provideMockActions(() => actions$)
      ]
    });

    effects = TestBed.get<KatalogEffects>(KatalogEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });
});
