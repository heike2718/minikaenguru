import { createReducer, Action, on } from '@ngrx/store';
import * as PrivatveranstalterActions from './privatveranstalter.actions';
import { Privatveranstalter } from '../../wettbewerb/wettbewerb.model';


export const privatveranstalterFeatureKey = 'mkv-app-privatveranstalter';

export interface PrivatveranstalterState {
	readonly veranstalter: Privatveranstalter;
	readonly aktuelleTeilnahmeGeladen: boolean;
	readonly loading: boolean;
}

const initialPrivatveranstalterState: PrivatveranstalterState = {
	veranstalter: undefined,
	aktuelleTeilnahmeGeladen: false,
	loading: false
};

const privatveranstalterReducer = createReducer(initialPrivatveranstalterState,

	on(PrivatveranstalterActions.startLoading, (state, _action) => {

		return { ...state, loading: true };
	}),


	on(PrivatveranstalterActions.finishedWithError, (state, _action) => {

		return { ...state, loading: false };
	}),


	on(PrivatveranstalterActions.privatveranstalterGeladen, (state, action) => {

		return {
			...state,
			veranstalter: action.veranstalter,
			aktuelleTeilnahmeGeladen: true,
			loading: false
		};
	}),

	on(PrivatveranstalterActions.privatveranstalterAngemeldet, (state, action) => {

		const neuerVeranstalter: Privatveranstalter = { ...state.veranstalter, aktuellAngemeldet: true, aktuelleTeilnahme: action.teilnahme };

		return {
			...state,
			veranstalter: neuerVeranstalter,
			aktuelleTeilnahmeGeladen: true,
			loading: false
		};
	}),

	on(PrivatveranstalterActions.resetPrivatveranstalter, (_state, _action) => {
		return initialPrivatveranstalterState;
	})

);

export function reducer(state: PrivatveranstalterState | undefined, action: Action) {
	return privatveranstalterReducer(state, action);
}
