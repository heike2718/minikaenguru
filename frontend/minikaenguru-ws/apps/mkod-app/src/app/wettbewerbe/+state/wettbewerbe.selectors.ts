import { createFeatureSelector, createSelector } from '@ngrx/store';
import { WettbewerbeMap } from '../wettbewerb.model';
import * as fromWettbewerbe from './wettbewerbe.reducer';

export const wettbewerbeState = createFeatureSelector<fromWettbewerbe.WettbewerbeState>(fromWettbewerbe.wettbewerbeFeatureKey);

export const wettbewerbeLoading = createSelector(wettbewerbeState, s => s.loading);
export const wettbewerbeLaded = createSelector(wettbewerbeState, s => s.wettbewerbeLoaded);
export const wettbewerbe = createSelector(wettbewerbeState, s => new WettbewerbeMap(s.wettbewerbeMap).toArray());
export const selectedWettbewerb = createSelector(wettbewerbeState, s => s.selectedWettbewerb);

