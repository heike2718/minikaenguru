import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromTeilnahmen from './teilnahmen.reducer';

export const teilnahmenState = createFeatureSelector<fromTeilnahmen.TeilnahmenState>(fromTeilnahmen.teilnahmenFeatureKey);

export const selectTeilnahmenummerAndName = createSelector(teilnahmenState, s => s.teilnahmenummerAndName);

export const anonymisierteTeilnahmenGeladen = createSelector(teilnahmenState, s => s.anonymisierteTeilnahmenGeladen);
export const anonymisierteTeilnahmen = createSelector(teilnahmenState, s => s.anonymisierteTeilnahmen);

export const loading = createSelector(teilnahmenState, s => s.loading);
