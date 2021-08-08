import { createFeatureSelector, createSelector } from '@ngrx/store';
import { SchuleUploadModel } from '../schulteilnahmen.model'
import * as fromSchuleilnahmenReducer from './schulteilnahmen.reducer';

export const schulteilnahmenState = createFeatureSelector<fromSchuleilnahmenReducer.SchulteilnahmenState>(fromSchuleilnahmenReducer.schulteilnahmenFeatureKey);

export const schulenMap = createSelector(schulteilnahmenState, s => s.schulenMap);
export const selectedSchule = createSelector(schulteilnahmenState, s => s.selectedSchule);
export const selectedTeilnahme = createSelector(schulteilnahmenState, s => s.selectedTeilnahme);
export const schuleLoading = createSelector(schulteilnahmenState, s => s.loading);
export const fehlermeldungen = createSelector(schulteilnahmenState, s => s.fehlermeldungenUploadReport);

export const schuleUploadModel = createSelector(selectedSchule, selectedTeilnahme, (s, t) => {
	if (s && t) {
		return {kuerzel: s.kuerzel, katalogData: s.katalogData, jahr: t.identifier.jahr, anzahlKinder: t.anzahlKinder} as SchuleUploadModel;
	} else {
		return undefined;
	}
});
