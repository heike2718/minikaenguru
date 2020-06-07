import { Schule } from '../schulen.model';
import * as SchulenActions from './schulen.actions';
import { Action, createReducer, on } from '@ngrx/store';

export const schulenFeatureKey = 'mkv-app-schulen';

export interface SchulenState {
	readonly schulen: Schule[];
	readonly selectedSchule: Schule;
	readonly schulenLoaded: boolean;
};

const initialSchulenState: SchulenState = {
	schulen: [],
	selectedSchule: undefined,
	schulenLoaded: false
};

const schulenReducer = createReducer(initialSchulenState,

	on(SchulenActions.schulenLoaded, (state, action) => {

		return { ...state, schulen: action.schulen, selectedSchule: undefined, schulenLoaded: true }
	}),

	// TODO: hier einen SideEffect implementieren, der die Details der Schule vom Server läd.
	on(SchulenActions.selectSchule, (state, action) => {
		return {...state, selectedSchule: action.schule}
	}),
	on(SchulenActions.unselectSchule, (state, _action) => {
		return {...state, selectedSchule: undefined}
	}),
	on(SchulenActions.resetSchulen,(_state, _action) => {
		return initialSchulenState;
	})
);

export function reducer(state: SchulenState | undefined, action: Action) {
	return schulenReducer(state, action);
}


