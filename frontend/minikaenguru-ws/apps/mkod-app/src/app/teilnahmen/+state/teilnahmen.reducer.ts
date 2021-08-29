import { Action, createReducer, on } from '@ngrx/store';
import { Anmeldungen, AnmeldungenMap, AnmeldungenWithID, Anmeldungsitem, WettbewerbStatus } from '../../shared/beteiligungen.model';
import * as TeilnahmenActions from './teilnahmen.actions';

export const teilnahmenFeatureKey = 'mkod-app-teilnahmen';

export interface TeilnahmenState {
    readonly anmeldungenMap: AnmeldungenWithID[],
    readonly selectedAnmeldungen: Anmeldungen;
	readonly selectedItem: Anmeldungsitem,
    readonly loading: boolean;
}

const initalTeilnahmenState: TeilnahmenState = {
    anmeldungenMap: [],
    selectedAnmeldungen: undefined,
    selectedItem: undefined,
    loading: false
};

const teilnahmenReducer = createReducer(initalTeilnahmenState,

	on(TeilnahmenActions.loadTeilnhahmen, (state, _action) => {

		return { ...state, loading: true }
	}),

    on(TeilnahmenActions.teilnahmenLoaded, (state, action) => {

        const neueMap = new AnmeldungenMap(state.anmeldungenMap).merge(action.anmeldungen);
		return { ...state, loading: false, anmeldungenMap: neueMap, selectedAnmeldungen: undefined, selectedItem: undefined};
    }),

    on(TeilnahmenActions.finishedWithError, (state, action) => {
        return { ...state, loading: false, selectedAnmeldungen: undefined, selectedItem: undefined};
    }),

    on(TeilnahmenActions.reset, (_state, _action) => {
        return initalTeilnahmenState;
    })
);

export function reducer(state: TeilnahmenState | undefined, action: Action) {
	return teilnahmenReducer(state, action);
};

