import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AnmeldungenMap } from '../../shared/beteiligungen.model';
import * as fromTeilnahmen from './teilnahmen.reducer';


export const teilnahmenState = createFeatureSelector<fromTeilnahmen.TeilnahmenState>(fromTeilnahmen.teilnahmenFeatureKey);

export const anmeldungenMap = createSelector(teilnahmenState, s => s.anmeldungenMap);
export const anmeldungen = createSelector(anmeldungenMap, m => new AnmeldungenMap(m).toArray());
