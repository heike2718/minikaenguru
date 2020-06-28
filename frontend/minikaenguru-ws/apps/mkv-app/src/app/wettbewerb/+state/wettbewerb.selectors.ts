import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromWettbewerb from './wettbewerb.reducer';

export const wettbewerbState = createFeatureSelector<fromWettbewerb.WettbewerbState>(fromWettbewerb.wettbewerbFeatureKey);

export const aktuellerWettbewerb = createSelector(wettbewerbState, s => s.aktuellerWettbewerb);


