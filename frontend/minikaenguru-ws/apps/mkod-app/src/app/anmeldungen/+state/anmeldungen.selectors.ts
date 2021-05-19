import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAnmeldungen from './anmeldungen.reducer';

export const anmeldungenState = createFeatureSelector<fromAnmeldungen.AnmeldungenState>(fromAnmeldungen.anmeldungenFeatureKey);

export const anmeldungenLoading = createSelector(anmeldungenState, s => s.loading);
export const anmeldungenLoaded = createSelector(anmeldungenState, s => s.anmeldungenLoaded);


