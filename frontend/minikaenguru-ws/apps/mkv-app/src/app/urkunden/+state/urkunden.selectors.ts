import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromUrkunden from './urkunden.reducer';

const urkundenState = createFeatureSelector<fromUrkunden.UrkundenState>(fromUrkunden.urkundenFeatureKey);

export const loading = createSelector(urkundenState, s => s.loading);
export const urkundenauftrag = createSelector(urkundenState, s => s.urkundenauftrag);
