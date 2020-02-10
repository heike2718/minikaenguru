import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKatalog from './katalog.reducer';

export const selectKatalogState = createFeatureSelector<fromKatalog.State>(
  fromKatalog.katalogFeatureKey
);
