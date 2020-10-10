import { Action, createReducer, on } from '@ngrx/store';
import * as SchulteilnahmenActions from './schulteilnahmen.actions';
import { SchuleAdminOverviewWithID, SchuleAdminOverview, SchulenOverviewMap } from '../schulteilnahmen.model';

export const schulteilnahmenFeatureKey = 'mk-admin-app-schulteilnahmen';

export interface SchulteilnahmenState {
	schulenMap: SchuleAdminOverviewWithID[],
	selectedSchule: SchuleAdminOverview
	loading: boolean
};

const initialSchulteilnahmenState: SchulteilnahmenState = {
	schulenMap: [],
	selectedSchule: undefined,
	loading: false
};

const schulteilnahmenReducer = createReducer(initialSchulteilnahmenState,

	on(SchulteilnahmenActions.resetSchulteilnahmen, (_state, _action) => {

		return initialSchulteilnahmenState;

	}),

	on(SchulteilnahmenActions.schuleOverviewLoaded, (state, action) => {

		const neueSchulenMap = new SchulenOverviewMap(state.schulenMap).add(action.schuleAdminOverview);

		return {
			...state, loading: false, schulenMap: neueSchulenMap, selectedSchule: action.schuleAdminOverview
		};

	}),
);

export function reducer(state: SchulteilnahmenState | undefined, action: Action) {

	return schulteilnahmenReducer(state, action);
}


