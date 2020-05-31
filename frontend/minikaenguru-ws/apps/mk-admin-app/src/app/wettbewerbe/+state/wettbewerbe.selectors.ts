import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromWettbewerbe from './wettbewerbe.reducer';
import { wettbewerbeWithIDArrayToWettbewerbeArray, findWettbewerbMitId } from '../wettbewerbe.model';

export const wettbewerbeState = createFeatureSelector<fromWettbewerbe.WettbewerbeState>(fromWettbewerbe.wettbewerbeFeatureKey);

export const wettbewerbeMap = createSelector(wettbewerbeState, s => s.wettbewerbeMap);
export const wettbewerbe = createSelector(wettbewerbeState, s => wettbewerbeWithIDArrayToWettbewerbeArray(s.wettbewerbeMap));
export const selectedWettbewerbsjahr= createSelector(wettbewerbeState, s => s.selectedJahr);
export const wettbewerbeLoaded = createSelector(wettbewerbeState, s => s.wettbewerbeLoaded);
export const selectedWettbewerb = createSelector(wettbewerbeMap, selectedWettbewerbsjahr, (sm, sj) => findWettbewerbMitId(sm, sj));
export const saveOutcome = createSelector(wettbewerbeState, s => s.saveOutcome);
export const wettbewerbEditorModel = createSelector(wettbewerbeState, s => s.wettbewerbEditorModel);


// createSelector<State, S1, S2, Result>(s1: Selector<State, S1>, s2: Selector<State, S2>, projector: (s1: S1, s2: S2) => Result): MemoizedSelector<State, Result>;


