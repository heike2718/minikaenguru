import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromPrivatveranstalter from './privatveranstalter.reducer';

export const privatveranstalterState = createFeatureSelector<fromPrivatveranstalter.PrivatveranstalterState>(fromPrivatveranstalter.privatveranstalterFeatureKey);

export const aktuelleangemeldet = createSelector(privatveranstalterState, s => s.aktuellAngemeldet);
export const aktuelleTeilnahmeGeladen = createSelector(privatveranstalterState, s => s.aktuelleTeilnahmeGeladen);
export const hatZugangZuUnterlagen = createSelector(privatveranstalterState, s => s.hatZugangZuUnterlangen);
export const aktuellePrivatteilnahme = createSelector(privatveranstalterState, s => s.aktuelleTeilnahme);
export const loading = createSelector(privatveranstalterState, s => s.loading);

export const kinderGeladen = createSelector(aktuellePrivatteilnahme, t => t.kinderGeladen);


