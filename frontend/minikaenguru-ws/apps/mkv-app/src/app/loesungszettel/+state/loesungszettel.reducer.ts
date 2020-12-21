import { createReducer, Action, on } from '@ngrx/store';
import { Loesungszettel, LoesungszettelWithID, LoesungszettelMap } from '../loesungszettel.model';
import * as LoesungszettelActions from './loesungszettel.actions';


export const loesungszettelFeatureKey = 'mkv-app-loesungszettel';

export interface LoesungszettelState {

	readonly loading: boolean;
	readonly loesungszettelMap: LoesungszettelWithID[];
	readonly selectedLoesungszettel: Loesungszettel;


};

const initialLoesungszettelState: LoesungszettelState = {
	loading: false,
	loesungszettelMap: [],
	selectedLoesungszettel: undefined
};


const loesungszettelReducer = createReducer(initialLoesungszettelState,

	on(LoesungszettelActions.startLoading, (state, _action) => {

		return { ...state, loading: true };
	}),

	on(LoesungszettelActions.finishedWithError, (state, _action) => {

		return { ...state, loading: false };
	}),

	on(LoesungszettelActions.loesungszettelLoaded, (state, action) => {

		const neueMap = new LoesungszettelMap(state.loesungszettelMap).merge(action.loesungszettel);


		return { ...state, loading: false, loesungszettelMap: neueMap, selectedLoesungszettel: undefined };
	}),

	on(LoesungszettelActions.loesungszettelSelected, (state, action) => {

		return { ...state, loading: false, selectedLoesungszettel: action.loesungszettel };
	}),

	on(LoesungszettelActions.resetModule, (_state, _action) => {
		return initialLoesungszettelState;
	})
);

export function reducer(state: LoesungszettelState | undefined, action: Action) {
	return loesungszettelReducer(state, action);
};

