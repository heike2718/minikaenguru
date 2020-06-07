import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromTeilnahmen from './teilnahmen.reducer';

export const teilnahmenState = createFeatureSelector<fromTeilnahmen.TeilnahmenState>(fromTeilnahmen.teilnahmenFeatureKey);

export const aktuellerWettbewerb = createSelector(teilnahmenState, s => s.aktuellerWettbewerb);

