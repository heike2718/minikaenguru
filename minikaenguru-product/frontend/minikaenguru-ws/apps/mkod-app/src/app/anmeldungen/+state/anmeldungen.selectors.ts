import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAnmeldungen from './anmeldungen.reducer';

export const anmeldungenState = createFeatureSelector<fromAnmeldungen.AnmeldungenState>(fromAnmeldungen.anmeldungenFeatureKey);


