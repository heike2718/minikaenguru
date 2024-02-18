import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVersandauftraege from './versandauftraege.reducer';

const newsletterState = createFeatureSelector<fromVersandauftraege.VersandauftraegeState>(fromVersandauftraege.versandauftraegeFeatureKey);

export const versandauftraege = createSelector(newsletterState, s => s.versandauftraege);
export const selectedVersandauftrag = createSelector(newsletterState, s => s.selecteVersandauftrag);

