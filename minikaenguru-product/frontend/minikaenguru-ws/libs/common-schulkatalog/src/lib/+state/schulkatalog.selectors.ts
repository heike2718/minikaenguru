import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromSchulkatalog from './schulkatalog.reducer';
import { KatalogItem, Katalogtyp } from '../domain/entities';

export const schulkatalogState = createFeatureSelector<fromSchulkatalog.SchulkatalogState>(
  fromSchulkatalog.schulkatalogFeatureKey
);

export const katalogGuiModel = createSelector(schulkatalogState, s => s.guiModel);
export const katalogItems = createSelector(schulkatalogState, s => s.loadedKatalogItems);
export const selectedKatalogtyp = createSelector(schulkatalogState, s => s.currentKatalogtyp);
export const selectedKatalogItem = createSelector(schulkatalogState, s => s.selectedKatalogItem);
export const searchTerm = createSelector(schulkatalogState, s => s.searchTerm);

export const katalogAntragSuccess = createSelector(katalogGuiModel, m => m.katalogantragSuccess);

export const selectedItemAndSelectedTyp = createSelector(selectedKatalogItem, selectedKatalogtyp, (i?: KatalogItem, t?: Katalogtyp) => 
  {return {'item': i, 'typ': t}});

