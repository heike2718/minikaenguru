import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAktuelleMeldung from './aktuelle-meldung.reducer';

export const aktuelleMeldungState = createFeatureSelector<fromAktuelleMeldung.AktuelleMeldungState>(fromAktuelleMeldung.aktuelleMeldungFeatureKey);

export const aktuelleMeldungGeladen = createSelector(aktuelleMeldungState, s => s.aktuelleMeldungLoaded);

export const habenAktuelleMeldung = createSelector(aktuelleMeldungState, s => s.aktuelleMeldungNotEmpty);

export const aktuelleMeldung = createSelector(aktuelleMeldungState, s => s.aktuelleMeldung);

