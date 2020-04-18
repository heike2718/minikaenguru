import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromSchulkatalog from './schulkatalog.reducer';

export const selectSchulkatalogState = createFeatureSelector<fromSchulkatalog.SchulkatalogState>(
  fromSchulkatalog.schulkatalogFeatureKey
);

export const selectGuiModel = createSelector(selectSchulkatalogState, s => s.guiModel);
export const selectKatalogItems = createSelector(selectSchulkatalogState, s => s.loadedKatalogItems);
export const selectKatalogtyp = createSelector(selectSchulkatalogState, s => s.guiModel.currentKatalogtyp);
export const selectSelectedKatalogItem = createSelector(selectSchulkatalogState, s => s.selectedKatalogItem);
export const selectLoadingIndicator = createSelector(selectSchulkatalogState, s => s.guiModel.showLoadingIndicator);
export const selectSearchTerm = createSelector(selectSchulkatalogState, s => s.searchTerm);

