import { createReducer, Action, on, State } from '@ngrx/store';
import { Loesungszettel, LoesungszettelWithID, LoesungszettelMap } from '../loesungszettel.model';
import * as LoesungszettelActions from './loesungszettel.actions';
import { Loesungszettelzeile } from '@minikaenguru-ws/common-components';


export const loesungszettelFeatureKey = 'mkv-app-loesungszettel';

export interface LoesungszettelState {

	readonly loading: boolean;
	readonly loesungszettelMap: LoesungszettelWithID[];
	readonly selectedLoesungszettel?: Loesungszettel;


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


		const zettel: Loesungszettel | undefined = state.selectedLoesungszettel;

		if (zettel) {
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

			let neueMap = new LoesungszettelMap(state.loesungszettelMap).remove(neuerLoesungszettel.uuid);
			neueMap = new LoesungszettelMap(neueMap).merge(neuerLoesungszettel);

			return { ...state, loading: false, loesungszettelMap: neueMap,  selectedLoesungszettel: neuerLoesungszettel };

		}

		return {...state};
	}),

	on(LoesungszettelActions.loesungszettelSaved, (state, action) => {

		if (!state.selectedLoesungszettel) {
			return {...state};
		}

		const neuerLoesungszettel: Loesungszettel = { ...state.selectedLoesungszettel, uuid: action.loesungszettelNeu.loesungszettelId, zeilen: action.loesungszettelNeu.zeilen, version: action.loesungszettelNeu.version };

		let neueMap = new LoesungszettelMap(state.loesungszettelMap).remove(action.loesungszettelAlt.uuid);

			neueMap = new LoesungszettelMap(neueMap).merge(neuerLoesungszettel);

		return { ...state, loading: false, loesungszettelMap: neueMap, selectedLoesungszettel: neuerLoesungszettel };
	}),

	on(LoesungszettelActions.loesungszettelDeleted, (state, action) => {

		const neueMap = new LoesungszettelMap(state.loesungszettelMap).remove(action.loesungszettel.uuid);

		return { ...state, l: false, loesungszettelMap: neueMap, selectedLoesungszettel: undefined };


	}),

	on(LoesungszettelActions.kindDeleted, (state, action) => {

		const loesungszettelMitKind: Loesungszettel | undefined = new LoesungszettelMap(state.loesungszettelMap).findWithKindUuid(action.kindUuid);

		if (loesungszettelMitKind) {
			const neueMap = new LoesungszettelMap(state.loesungszettelMap).remove(loesungszettelMitKind.uuid);

			return {...state, loesungszettelMap: neueMap};
		}

		return state;

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

