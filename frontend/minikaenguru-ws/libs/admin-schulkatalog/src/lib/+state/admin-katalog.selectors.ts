import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromSchulkatalog from './admin-katalog.reducer';

const schulkatalogState = createFeatureSelector<fromSchulkatalog.AdminSchulkatalogState>(fromSchulkatalog.adminSchulkatalogFeatureKey);

export const laender = createSelector(schulkatalogState, state => state.laender);
export const laenderGeladen = createSelector(laender, laender => laender.length > 0);
export const selectedLand = createSelector(schulkatalogState, state => state.selectedLand);

export const orte = createSelector(schulkatalogState, state => state.orte);
export const orteGeladen = createSelector(orte, orte => orte.length > 0);
export const selectedOrt = createSelector(schulkatalogState, state => state.selectedOrt);

export const schulen = createSelector(schulkatalogState, state => state.schulen);
export const schulenGeladen = createSelector(schulen, schulen => schulen.length > 0);
export const selectedSchule = createSelector(schulkatalogState, state => state.selectedSchule);