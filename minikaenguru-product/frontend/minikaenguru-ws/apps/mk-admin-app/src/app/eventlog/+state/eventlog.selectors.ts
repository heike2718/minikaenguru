import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromEventlogReducer from './eventlog.reducer';

export const eventlogState = createFeatureSelector<fromEventlogReducer.EventlogState>(fromEventlogReducer.eventlogFeatureKey);

export const selectedDatum = createSelector(eventlogState, s => s.datum);
