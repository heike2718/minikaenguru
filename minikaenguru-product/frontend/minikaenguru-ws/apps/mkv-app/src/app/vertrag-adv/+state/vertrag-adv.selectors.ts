import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVertragAdv from './vertrag-adv.reducer';

export const vertragAdvState = createFeatureSelector<fromVertragAdv.VertragAdvEditorState>(fromVertragAdv.vertragAdvFeatureKey);

export const selectedSchule = createSelector(vertragAdvState, s => s.selectedSchule);
export const vertragAdvEditorModel = createSelector(vertragAdvState, s => s.editorModel);
export const submitDisabled = createSelector(vertragAdvState, s => s.submitDisabled);
export const saveInProgress = createSelector(vertragAdvState, s => s.saveInProgress);
