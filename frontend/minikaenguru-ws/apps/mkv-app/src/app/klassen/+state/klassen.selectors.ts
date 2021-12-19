import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKlassen from './klassen.reducer';
import { KlassenMap } from '../klassen.model';

export const klassenState = createFeatureSelector<fromKlassen.KlassenState>(fromKlassen.klassenFeatureKey);


export const klassenGeladen = createSelector(klassenState, s => s.klassenLoaded);
export const klassen = createSelector(klassenState, s => new KlassenMap(s.klassenMap).toArray());
export const anzahlKlassen = createSelector(klassenState, s => s.klassenMap.length);
export const klasseUIModel = createSelector(klassenState, s => s.klasseUIModel);
export const klassenMap = createSelector(klassenState, s => s.klassenMap);
export const selectedKlasse = createSelector(klassenState, s => s.selectedKlasse);
export const klassenimportReport = createSelector(klassenState, s => s.importReport);

export const anzahlKinder = createSelector(klassenState, s => new KlassenMap(s.klassenMap).countKinder() );
export const anzahlLoesungszettel = createSelector(klassenState, s => new KlassenMap(s.klassenMap).countLoesungszettel() );
