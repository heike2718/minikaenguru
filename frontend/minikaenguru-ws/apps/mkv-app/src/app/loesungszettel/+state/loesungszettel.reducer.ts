import { createReducer, Action, on, State } from '@ngrx/store';
import { Loesungszettel, LoesungszettelWithID, LoesungszettelMap, Loesungszettelzeile } from '../loesungszettel.model';
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

	on(LoesungszettelActions.newLoesungszettelCreated, (state, action) => {

		return { ...state, selectedLoesungszettel: action.loesungszettel }
	}),

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

	on(LoesungszettelActions.loesungszettelChanged, (state, action) => {

		const zettel: Loesungszettel = state.selectedLoesungszettel;

		const zeile: Loesungszettelzeile = action.zeile;

		const neueZeilen: Loesungszettelzeile[] = [];

		for (let i = 0; i < zettel.zeilen.length; i++) {
			const z: Loesungszettelzeile = zettel.zeilen[i];
			if (z.index !== zeile.index) {
				neueZeilen.push(z);
			} else {
				neueZeilen.push(zeile);
			}
		}

		const neuerLoesungszettel = { ...zettel, zeilen: neueZeilen };

		return { ...state, selectedLoesungszettel: neuerLoesungszettel };

	}),

	on(LoesungszettelActions.loesungszettelSaved, (state, action) => {


		if (action.loesungszettelAlt.uuid === 'neu') {

			const neuerLoesungszettel = { ...state.selectedLoesungszettel, uuid: action.loesungszettelUuidNeu };

			let neueMap = new LoesungszettelMap(state.loesungszettelMap).remove(action.loesungszettelAlt.uuid);
			neueMap = new LoesungszettelMap(neueMap).merge(neuerLoesungszettel);

			return { ...state, loading: false, loesungszettelMap: neueMap, selectedLoesungszettel: neuerLoesungszettel };

		} else {

			return { ...state, loading: false };
		}
	}),

	on(LoesungszettelActions.loesungszettelDeleted, (state, action) => {

		const neueMap = new LoesungszettelMap(state.loesungszettelMap).remove(action.loesungszettel.uuid);

		return { ...state, l: false, loesungszettelMap: neueMap, selectedLoesungszettel: undefined };


	}),

	on(LoesungszettelActions.editLoesungszettelCancelled, (state, _action) => {

		return { ...state, loading: false, selectedLoesungszettel: undefined };
	}),

	on(LoesungszettelActions.resetModule, (_state, _action) => {
		return initialLoesungszettelState;
	})
);

export function reducer(state: LoesungszettelState | undefined, action: Action) {
	return loesungszettelReducer(state, action);
};

