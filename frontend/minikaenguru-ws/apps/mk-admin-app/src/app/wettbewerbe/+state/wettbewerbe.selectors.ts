import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromWettbewerbe from './wettbewerbe.reducer';

export const wettbewerbeState = createFeatureSelector<fromWettbewerbe.WettbewerbeState>(fromWettbewerbe.wettbewerbeFeatureKey);

export const wettbewerbe = createSelector(wettbewerbeState, s => s.wettbewerbe);
export const wettbewerbeLoaded = createSelector(wettbewerbeState, s => s.wettbewerbeLoaded);
export const selectedWettbewerb = createSelector(wettbewerbeState, s => s.selectedWettbewerb);
