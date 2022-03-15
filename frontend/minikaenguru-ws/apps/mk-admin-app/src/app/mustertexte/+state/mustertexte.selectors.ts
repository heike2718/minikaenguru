import { createFeatureSelector, createSelector } from '@ngrx/store';
import { MustertexteMap } from '../mustertexte.model';
import * as fromMustertexte from './mustertexte.reducer';

const mustertexteState = createFeatureSelector<fromMustertexte.MustertexteState>(fromMustertexte.mustertexteFeatureKey);

const mustertexteMap = createSelector(mustertexteState, s => s.mustertexteMap);

export const mustertexte = createSelector(mustertexteMap, m => new MustertexteMap(m).toArray());
export const selectedMustertext = createSelector(mustertexteState, s => s.selectedMustertext);
export const mustertextEditoModel = createSelector(mustertexteState, s => s.mustertextEditorModel);
export const mustertexteLoaded = createSelector(mustertexteState, s => s.mustertexteLoaded);
export const loading = createSelector(mustertexteState, s => s.loading);
