import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKatalogpflege from './katalogpflege.reducer';
import { katalogpflegeItemWithIDToArray, SchulePayload, KatalogpflegeItem } from '../katalogpflege.model';

export const katalogflegeState = createFeatureSelector<fromKatalogpflege.KatalogpflegeState>(fromKatalogpflege.katalogpflegeFeatureKey);

export const kataloge = createSelector(katalogflegeState, s => s.kataloge);
export const laender = createSelector(katalogflegeState, s => katalogpflegeItemWithIDToArray(s.kataloge.laender));
export const orte = createSelector(katalogflegeState, s => s.filteredOrte);
export const schulen = createSelector(katalogflegeState, s => s.filteredSchulen);
export const selectedItem = createSelector(katalogflegeState, s => s.selectedKatalogItem);
export const loading = createSelector(katalogflegeState, s => s.showLoadingIndicator);

const schulePayload = createSelector(katalogflegeState, s => s.schulePayload);
const isModusCreate = createSelector(katalogflegeState, s => s.modusCreate);

export const editSchuleInput = createSelector(schulePayload, isModusCreate, selectedItem, loading, (p, m, it, loading) => combineSchulePayloadWithModus(p, m, it, loading));



function combineSchulePayloadWithModus(p: SchulePayload, m: boolean, it: KatalogpflegeItem, loading: boolean): { 'payload': SchulePayload, 'isModusCreate': boolean, 'selectedItem': KatalogpflegeItem, 'loading': boolean } {
	return {
		payload: p,
		isModusCreate: m,
		selectedItem: it,
		loading: loading
	};
}
