import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromPrivatauswertung from './privatauswertung.reducer';
import { KinderMap } from '../privatauswertung.model';

export const privatauswertungState = createFeatureSelector<fromPrivatauswertung.PrivatauswertungState>(fromPrivatauswertung.privatauswertungFeatureKey);

export const kindEditorModel = createSelector(privatauswertungState, s => s.editorModel);
export const kinderMap = createSelector(privatauswertungState, s => s.kinderMap);
export const kinder = createSelector(privatauswertungState, s => new KinderMap(s.kinderMap).toArray());
export const kinderGeladen = createSelector(privatauswertungState, s => s.kinderLoaded);


