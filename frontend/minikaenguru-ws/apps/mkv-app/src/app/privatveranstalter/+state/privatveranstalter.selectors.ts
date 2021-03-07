import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromPrivatveranstalter from './privatveranstalter.reducer';

export const privatveranstalterState = createFeatureSelector<fromPrivatveranstalter.PrivatveranstalterState>(fromPrivatveranstalter.privatveranstalterFeatureKey);

export const privatveranstalter = createSelector(privatveranstalterState, s => s.veranstalter);

export const zugangUnterlagen = createSelector(privatveranstalter, v => v && v.hatZugangZuUnterlagen);
export const aktuelleTeilnahmeGeladen = createSelector(privatveranstalterState, s => s.aktuelleTeilnahmeGeladen);
export const aktuellePrivatteilnahme = createSelector(privatveranstalter, s => s.aktuelleTeilnahme);
export const loading = createSelector(privatveranstalterState, s => s.loading);


