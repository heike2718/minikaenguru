import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromloesungszettel from './loesungszettel.reducer';

const loesungszettelSelector = createFeatureSelector<fromloesungszettel.LoesungszettelState>(fromloesungszettel.loesungszettelFeatureKey);

export const loading = createSelector(loesungszettelSelector, s => s.loading);
export const anzahlLoesungszettel = createSelector(loesungszettelSelector, s => s.anzahlLoesungszettel);
export const pageContent = createSelector(loesungszettelSelector, s => s.pageContent);
