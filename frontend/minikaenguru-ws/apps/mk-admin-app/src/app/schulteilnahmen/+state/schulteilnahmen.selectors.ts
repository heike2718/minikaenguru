import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromSchuleilnahmenReducer from './schulteilnahmen.reducer';

export const schulteilnahmenState = createFeatureSelector<fromSchuleilnahmenReducer.SchulteilnahmenState>(fromSchuleilnahmenReducer.schulteilnahmenFeatureKey);

export const schulenMap = createSelector(schulteilnahmenState, s => s.schulenMap);
export const selectedSchule = createSelector(schulteilnahmenState, s => s.selectedSchule);
export const schuleLoading = createSelector(schulteilnahmenState, s => s.loading);
