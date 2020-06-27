import { createReducer, Action, on } from '@ngrx/store';
import * as PrivatveranstalterActions from './privatveranstalter.actions';
import { Privatteilnahme, AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';


export const privatveranstalterFeatureKey = 'mkv-app-privatveranstalter';

export interface PrivatveranstalterState {
	readonly hatZugangZuUnterlangen: boolean;
	readonly aktuellAngemeldet: boolean;
	readonly aktuelleTeilnahmeGeladen: boolean;
	readonly anzahlVergangeneTeilnahmen: number;
	readonly aktuelleTeilnahme: Privatteilnahme;
	readonly vergangeneTeilnahmenGeladen: boolean;
	readonly vergangeneTeilnahmen: AnonymisierteTeilnahme[];
	readonly loading: boolean;
}

const initialPrivatveranstalterState: PrivatveranstalterState = {
	hatZugangZuUnterlangen: undefined,
	aktuellAngemeldet: undefined,
	aktuelleTeilnahmeGeladen: false,
	anzahlVergangeneTeilnahmen: 0,
	aktuelleTeilnahme: undefined,
	vergangeneTeilnahmenGeladen: false,
	vergangeneTeilnahmen: [],
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
			hatZugangZuUnterlangen: action.veranstalter.hatZugangZuUnterlangen,
			aktuelleTeilnahmeGeladen: true,
			anzahlVergangeneTeilnahmen: action.veranstalter.anzahlVergangeneTeilnahmen,
			aktuelleTeilnahme: action.veranstalter.aktuelleTeilnahme,
			aktuellAngemeldet: action.veranstalter.aktuellAngemeldet,
			loading: false
		};
	}),

	on(PrivatveranstalterActions.privatveranstalterAngemeldet, (state, action) => {

		return {
			...state,
			aktuellAngemeldet: true,
			aktuelleTeilnahmeGeladen: true,
			aktuelleTeilnahme: action.teilnahme
		};
	}),

	on(PrivatveranstalterActions.privatteilnahmeCreated, (state, action) => {
		return { ...state, aktuelleTeilnahme: action.teilnahme, aktuelleTeilnahmeGeladen: true };
	}),

	on(PrivatveranstalterActions.resetPrivatveranstalter, (_state, _action) => {
		return initialPrivatveranstalterState;
	})

);

export function reducer(state: PrivatveranstalterState | undefined, action: Action) {
	return privatveranstalterReducer(state, action);
}
