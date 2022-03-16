import { createFeatureSelector, createSelector } from '@ngrx/store';
import { MustertexteMap } from '../mustertexte.model';
import * as fromMustertexte from './mustertexte.reducer';

const mustertexteState = createFeatureSelector<fromMustertexte.MustertexteState>(fromMustertexte.mustertexteFeatureKey);

const filteredMap = createSelector(mustertexteState, s => s.filteredMustertexte);

export const filterKategorie = createSelector(mustertexteState, s => s.filterKategorie);
export const mustertexte = createSelector(filteredMap, m => new MustertexteMap(m).toArray());
export const selectedMustertext = createSelector(mustertexteState, s => s.selectedMustertext);
export const mustertextEditoModel = createSelector(mustertexteState, s => s.mustertextEditorModel);
export const mustertexteLoaded = createSelector(mustertexteState, s => s.mustertexteLoaded);
export const loading = createSelector(mustertexteState, s => s.loading);

