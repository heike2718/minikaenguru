import { createReducer, Action, on } from '@ngrx/store';
import * as PrivatveranstalterActions from './privatveranstalter.actions';
import { Privatveranstalter } from '../../wettbewerb/wettbewerb.model';


export const privatveranstalterFeatureKey = 'mkv-app-privatveranstalter';

export interface PrivatveranstalterState {
	readonly veranstalter?: Privatveranstalter;
	readonly aktuelleTeilnahmeGeladen: boolean;
}

const initialPrivatveranstalterState: PrivatveranstalterState = {
	veranstalter: undefined,
	aktuelleTeilnahmeGeladen: false,
};

const privatveranstalterReducer = createReducer(initialPrivatveranstalterState,

	on(PrivatveranstalterActions.finishedWithError, (state, _action) => {

		return { ...state };
	}),


	on(PrivatveranstalterActions.privatveranstalterGeladen, (state, action) => {

		return {
			...state,
			veranstalter: action.veranstalter,
			aktuelleTeilnahmeGeladen: true,
			loading: false
		};
	}),

	on(PrivatveranstalterActions.aboNewsletterChanged, (state, _action) => {

		if (!state.veranstalter) {
			return {...state};
		}

		const abonniert = !state.veranstalter.newsletterAbonniert;
		const neuerVeranstalter = {...state.veranstalter, newsletterAbonniert: abonniert};
		return { ...state, veranstalter: neuerVeranstalter };
	}),


	on(PrivatveranstalterActions.privatveranstalterAngemeldet, (state, action) => {

		if (!state.veranstalter) {
			return {...state};
		}

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
