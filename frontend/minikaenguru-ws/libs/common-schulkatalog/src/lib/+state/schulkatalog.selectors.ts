import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromSchulkatalog from './schulkatalog.reducer';

export const schulkatalogState = createFeatureSelector<fromSchulkatalog.SchulkatalogState>(
  fromSchulkatalog.schulkatalogFeatureKey
);

export const katalogGuiModel = createSelector(schulkatalogState, s => s.guiModel);
export const katalogItems = createSelector(schulkatalogState, s => s.loadedKatalogItems);
export const selectedKatalogtyp = createSelector(schulkatalogState, s => s.guiModel.currentKatalogtyp);
export const selectedKatalogItem = createSelector(schulkatalogState, s => s.selectedKatalogItem);
export const showLoadingIndicator = createSelector(schulkatalogState, s => s.guiModel.showLoadingIndicator);
export const searchTerm = createSelector(schulkatalogState, s => s.searchTerm);

