import { Schule, SchuleDashboardModel } from '../schulen.model';
import * as SchulenActions from './schulen.actions';
import { Action, createReducer, on } from '@ngrx/store';

export const schulenFeatureKey = 'mkv-app-schulen';

export interface SchulenState {
	readonly schulen: Schule[];
	readonly selectedSchule: Schule;
	readonly schuleDashboadModel: SchuleDashboardModel;
	readonly schulenLoaded: boolean;
	readonly loading: boolean;
};

const initialSchulenState: SchulenState = {
	schulen: [],
	selectedSchule: undefined,
	schuleDashboadModel: undefined,
	schulenLoaded: false,
	loading: false
};

const schulenReducer = createReducer(initialSchulenState,

	on(SchulenActions.startLoading, (state, _action) => {
		return {...state, loading: true};
	}),

	on(SchulenActions.finishedWithError, (state, _action) => {
		return {...state, loading: false};
	}),

	on(SchulenActions.schulenLoaded, (state, action) => {

		return { ...state, schulen: action.schulen, selectedSchule: undefined, schulenLoaded: true, loading: false }
	}),

	on(SchulenActions.selectSchule, (state, action) => {
		return {...state, selectedSchule: action.schule}
	}),

	on(SchulenActions.schuleDetailsLoaded, (state, action) => {

		return {...state, selectedSchule: action.schule, schuleDashboadModel: action.details, loading: false};
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


