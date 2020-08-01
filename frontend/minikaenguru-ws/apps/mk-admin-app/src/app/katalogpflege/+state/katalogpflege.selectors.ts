import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKatalogpflege from './katalogpflege.reducer';
import { KatalogpflegeItem, KatalogPflegeItemsMap } from '../katalogpflege.model';

export const katalogflegeState = createFeatureSelector<fromKatalogpflege.KatalogpflegeState>(fromKatalogpflege.katalogpflegeFeatureKey);

export const kataloge = createSelector(katalogflegeState, s => s.kataloge);
export const laender = createSelector(katalogflegeState, s => new KatalogPflegeItemsMap(s.kataloge.laender).toArray());
export const orte = createSelector(katalogflegeState, s => s.filteredOrte);
export const schulen = createSelector(katalogflegeState, s => s.filteredSchulen);
export const selectedItem = createSelector(katalogflegeState, s => s.selectedKatalogItem);
export const loading = createSelector(katalogflegeState, s => s.showLoadingIndicator);

const schuleEditorModel = createSelector(katalogflegeState, s => s.schuleEditorModel);

export const editSchuleInput = createSelector(schuleEditorModel, selectedItem, loading, (e, it, lo) => combineSchulePayloadWithModus(e, it, lo));
export const editOrtInput = createSelector(katalogflegeState, s => s.ortEditorPayload);
export const editLandInput = createSelector(katalogflegeState, s => s.landEditorPayload);



function combineSchulePayloadWithModus(e: fromKatalogpflege.SchuleEditorModel, it: KatalogpflegeItem, lo: boolean): { 'schuleEditorModel': fromKatalogpflege.SchuleEditorModel, 'selectedItem': KatalogpflegeItem, 'loading': boolean } {
	return {
		schuleEditorModel: e,
		selectedItem: it,
		loading: lo
	};
}
