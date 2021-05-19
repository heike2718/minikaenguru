import { Anmeldungsitem, WettbewerbStatus, Anmeldungen } from '../anmeldungen.model';
import { Action, createReducer, on } from '@ngrx/store';
import * as AnmeldungenActions from './anmeldungen.actions';


export const anmeldungenFeatureKey = 'mkod-app-anmeldungen';

export interface AnmeldungenState {
	wettbewerbsjahr: string,
	statusWettbewerb: WettbewerbStatus,
	privatanmeldungen: Anmeldungsitem,
	schulanmeldungen: Anmeldungsitem,
	laender: Anmeldungsitem[],
	selectedItem: Anmeldungsitem,
	anmeldungenLoaded: boolean,
	loading: boolean
};

const initialAnmeldungenState: AnmeldungenState = {
	wettbewerbsjahr: undefined,
	statusWettbewerb: undefined,
	privatanmeldungen: undefined,
	schulanmeldungen: undefined,
	laender: [],
	selectedItem: undefined,
	anmeldungenLoaded: false,
	loading: false
};

const anmeldungenReducer = createReducer(initialAnmeldungenState,

	on(AnmeldungenActions.loadAnmeldungen, (_state, _action) => {
		return { ...initialAnmeldungenState, loading: true }
	}),

	on(AnmeldungenActions.anmeldungenLoaded, (state, action) => {

		const anmeldungen: Anmeldungen = action.anmeldungen;

		return anmeldungen ? {
			...state,
			loading: false,
			anmeldungenLoaded: true,
			wettbewerbsjahr: anmeldungen.wettbewerbsjahr,
			statusWettbewerb: anmeldungen.statusWettbewerb,
			privatanmeldungen: anmeldungen.privatanmeldungen,
			schulanmeldungen: anmeldungen.schulanmeldungen,
			laender: action.anmeldungen.laender

		} : initialAnmeldungenState;
	}),

	on(AnmeldungenActions.finishedWithError, (state, _action) => {
		return { ...state, loading: false };
	}),

	on(AnmeldungenActions.reset, (_state, _action) => {
		return initialAnmeldungenState;
	}),
);


export function reducer(state: AnmeldungenState | undefined, action: Action) {

	return anmeldungenReducer(state, action);

};


