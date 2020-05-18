import { Schule } from '../schulen.model';
import * as SchulenActions from './schulen.actions';
import { Action, createReducer, on } from '@ngrx/store';

export const schulenFeatureKey = 'schulen';

export interface SchulenState {
	schulen: Schule[];
	selectedSchule: Schule;
};

export const initialSchulenState = {
	schulen: [],
	selectedSchule: undefined
};

const schulenReducer = createReducer(initialSchulenState,

	on(SchulenActions.schulenLoaded, (state, action) => {

		return { ...state, schulen: action.schulen, selectedSchule: undefined }
	}),
);


