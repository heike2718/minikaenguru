import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKlassen from './klassen.reducer';
import { KlassenMap } from '../klassen.model';

export const klassenState = createFeatureSelector<fromKlassen.KlassenState>(fromKlassen.klassenFeatureKey);


export const klassenGeladen = createSelector(klassenState, s => s.klassenLoaded);
export const klassen = createSelector(klassenState, s => new KlassenMap(s.klassenMap).toArray());
export const anzahlKlassen = createSelector(klassenState, s => s.klassenMap.length);
