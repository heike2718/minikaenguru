import { createReducer, Action, on } from '@ngrx/store';
import * as PrivatveranstalterActions from './privatveranstalter.actions';
import { Privatteilnahme, AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';


export const privatveranstalterFeatureKey = 'mkv-app-privatveranstalter';

export interface PrivatveranstalterState {
	readonly hatZugangZuUnterlangen: boolean;
	readonly aktuelleTeilnahmeGeladen: boolean;
	readonly aktuelleTeilnahme: Privatteilnahme;
	readonly vergangeneTeilnahmenGeladen: boolean;
	readonly vergangeneTeilnahmen: AnonymisierteTeilnahme[];
}

const initialPrivatveranstalterState: PrivatveranstalterState = {
	hatZugangZuUnterlangen: undefined,
	aktuelleTeilnahmeGeladen: false,
	aktuelleTeilnahme: undefined,
	vergangeneTeilnahmenGeladen: false,
	vergangeneTeilnahmen: []
};

const privatveranstalterReducer = createReducer(initialPrivatveranstalterState,

	on(PrivatveranstalterActions.privatveranstalterGeladen, (state, action) => {
		return { ...state, hatZugangZuUnterlangen: action.veranstalter.hatZugangZuUnterlangen, aktuelleTeilnahme: action.veranstalter.aktuelleTeilnahme };
	}),

	on(PrivatveranstalterActions.privatteilnahmeCreated, (state, action) => {
		return {...state, aktuelleTeilnahme: action.teilnahme, aktuelleTeilnahmeGeladen: true};
	}),

	on(PrivatveranstalterActions.resetPrivatveranstalter, (_state, _action) => {
		return initialPrivatveranstalterState;
	})

);

export function reducer(state: PrivatveranstalterState | undefined, action: Action) {
	return privatveranstalterReducer(state, action);
}
