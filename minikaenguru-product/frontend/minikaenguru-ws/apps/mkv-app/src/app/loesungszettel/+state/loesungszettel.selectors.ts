import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromLoesungszettel from './loesungszettel.reducer';
import { LoesungszettelMap } from '../loesungszettel.model';

const loesungszettelState = createFeatureSelector<fromLoesungszettel.LoesungszettelState>(fromLoesungszettel.loesungszettelFeatureKey);

export const loading = createSelector(loesungszettelState, s => s.loading);
export const selectedLoesungszettel = createSelector(loesungszettelState, s => s.selectedLoesungszettel);
export const loesungszettelMap = createSelector(loesungszettelState, s => new LoesungszettelMap(s.loesungszettelMap));
