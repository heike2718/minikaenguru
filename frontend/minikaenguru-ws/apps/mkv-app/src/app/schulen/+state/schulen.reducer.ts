import { Schule } from '../schulen.model';
import * as SchulenActions from './schulen.actions';
import { Action, createReducer, on } from '@ngrx/store';

export const schulenFeatureKey = 'mkv-app-schulen';

export interface SchulenState {
	schulen: Schule[];
	selectedSchule: Schule;
	schulenLoaded: boolean;
};

export const initialSchulenState = {
	schulen: [],
	selectedSchule: undefined,
	schulenLoaded: false
};

const schulenReducer = createReducer(initialSchulenState,

	on(SchulenActions.schulenLoaded, (state, action) => {

		return { ...state, schulen: action.schulen, selectedSchule: undefined, schulenLoaded: true }
	}),
);

export function reducer(state: SchulenState | undefined, action: Action) {
	return schulenReducer(state, action);
}


