import { Schule, SchuleWithID, mergeSchulenMap, findSchuleMitId } from '../schulen.model';
import * as SchulenActions from './schulen.actions';
import { Action, createReducer, on } from '@ngrx/store';

export const schulenFeatureKey = 'mkv-app-schulen';

export interface SchulenState {
	readonly schulen: SchuleWithID[];
	readonly selectedSchule: Schule;
	readonly schulenLoaded: boolean;
	readonly loading: boolean;
};

const initialSchulenState: SchulenState = {
	schulen: [],
	selectedSchule: undefined,
	schulenLoaded: false,
	loading: false
};

const schulenReducer = createReducer(initialSchulenState,

	on(SchulenActions.startLoading, (state, _action) => {
		return { ...state, loading: true };
	}),

	on(SchulenActions.finishedWithError, (state, _action) => {
		return { ...state, loading: false };
	}),

	on(SchulenActions.schulenLoaded, (state, action) => {

		const schulen: Schule[] = action.schulen;
		const newMap: SchuleWithID[] = [];
		schulen.forEach(s => newMap.push({ kuerzel: s.kuerzel, schule: s }));

		return { ...state, schulen: newMap, selectedSchule: undefined, schulenLoaded: true, loading: false }
	}),

	on(SchulenActions.selectSchule, (state, action) => {
		return { ...state, selectedSchule: action.schule }
	}),

	on(SchulenActions.schuleDetailsLoaded, (state, action) => {

		const neueMap = mergeSchulenMap(state.schulen, action.schule);
		return { ...state, selectedSchule: action.schule, schulen: neueMap, loading: false };
	}),

	on(SchulenActions.restoreDetailsFromCache, (state, action) => {

		const schule = findSchuleMitId(state.schulen, action.kuerzel);

		if (schule != null) {
			return {...state, selectedSchule: schule};
		}

		return state;
	}),

	on(SchulenActions.deselectSchule, (state, _action) => {
		return { ...state, selectedSchule: undefined }
	}),

	on(SchulenActions.resetSchulen, (_state, _action) => {
		return initialSchulenState;
	})
);

export function reducer(state: SchulenState | undefined, action: Action) {
	return schulenReducer(state, action);
}


