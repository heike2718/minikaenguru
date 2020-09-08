import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromPrivatteilnahmen from './privatteilnahmen.reducer';

export const privatteilnahmenState = createFeatureSelector<fromPrivatteilnahmen.PrivatteilnahmenState>(fromPrivatteilnahmen.privatteilnahmenFeatureKey);

export const anonymisierteTeilnahmenGeladen = createSelector(privatteilnahmenState, s => s.anonymisierteTeilnahmenGeladen);
export const anonymisierteTeilnahmen = createSelector(privatteilnahmenState, s => s.anonymisierteTeilnahmen);

export const loading = createSelector(privatteilnahmenState, s => s.loading);
