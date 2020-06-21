import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromPrivatveranstalter from './privatveranstalter.reducer';

export const privatveranstalterState = createFeatureSelector<fromPrivatveranstalter.PrivatveranstalterState>(fromPrivatveranstalter.privatveranstalterFeatureKey);

export const hatZugangZuUnterlagen = createSelector(privatveranstalterState, s => s.hatZugangZuUnterlangen);
export const aktuellePrivatteilnahme = createSelector(privatveranstalterState, s => s.aktuelleTeilnahme);


