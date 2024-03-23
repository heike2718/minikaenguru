import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromKinder from './kinder.reducer';
import { KinderMap } from '../kinder.model';

const kinderState = createFeatureSelector<fromKinder.KinderState>(fromKinder.kinderFeatureKey);

export const teilnahmeIdentifier = createSelector(kinderState, s => s.teilnahmeIdentifier);
export const kindEditorModel = createSelector(kinderState, s => s.editorModel);
export const kinderMap = createSelector(kinderState, s => s.kinderMap);

export const kinder = createSelector(kinderState, s => new KinderMap(s.kinderMap).toArray());


export const kinderGeladen = createSelector(kinderState, s => s.kinderLoaded);
export const anzahlKinder = createSelector(kinderState, s => s.kinderMap.length);
export const duplikatwarnung = createSelector(kinderState, s => s.duplikatwarnung);
export const saveOutcome = createSelector(kinderState, s => s.saveOutcome);

export const selectedKind = createSelector(kinderState, s => s.selectedKind);


