import { Action, createReducer, on } from '@ngrx/store';
import { VertragAdvEditorModel } from '../vertrag-adv.model';
import * as VertragAdvActions from './vertrag-adv.actions';
import { Schule } from '../../lehrer/schulen/schulen.model';



export const vertragAdvFeatureKey = 'mkv-app-vertrag-adv';

export interface VertragAdvEditorState {
	selectedSchule: Schule,
	editorModel: VertragAdvEditorModel;
	submitDisabled: boolean;
	saveInProgress: boolean;
};

const initialVertragAdvState: VertragAdvEditorState = {
	selectedSchule: undefined,
	editorModel: undefined,
	submitDisabled: true,
	saveInProgress: false
};

const vertragAdvReducer = createReducer(initialVertragAdvState,

	on(VertragAdvActions.selectSchule, (state, action) => {
		return {...state, selectedSchule: action.schule};
	}),

	on(VertragAdvActions.editorModelInitialized, (state, action) => {

		return { ...state, editorModel: action.model, submitDisabled: true, saveInProgress: false };

	}),

	on(VertragAdvActions.formValidated, (state, action) => {

		return { ...state, submitDisabled: !action.valid };

	}),

	on(VertragAdvActions.submitStarted, (state, _action) => {
		return { ...state, submitDisabled: true, saveInProgress: true };
	}),

	on(VertragAdvActions.submitFinished, (state, _action) => {
		return { ...state, submitDisabled: true, saveInProgress: false };
	}),

	on(VertragAdvActions.editVertragFinished, (_state, _action) => {
		return initialVertragAdvState;
	}),



);

export function reducer(state: VertragAdvEditorState | undefined, action: Action) {
	return vertragAdvReducer(state, action)
}


