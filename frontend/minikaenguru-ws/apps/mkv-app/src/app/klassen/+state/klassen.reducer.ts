import { createReducer, Action, on } from '@ngrx/store';
import * as KlassenActions from './klassen.actions';
import { KlasseWithID } from '../klassen.model';


export const klassenFeatureKey = 'mkv-app-klassen';

export interface KlassenState {
	klassenMap: KlasseWithID[];
	klassenLoaded: boolean;
	loading: boolean;
};

const initialKlassenState: KlassenState = {
	klassenMap: [],
	klassenLoaded: false,
	loading: false
};

const klassenReducer = createReducer(initialKlassenState,

	on(KlassenActions.resetModule, (_state, _action) => {
		return initialKlassenState;
	}),

	on(KlassenActions.allKlassenLoaded, (state, action) => {

		const alle = action.klassen;
		const newMap = [];
		alle.forEach(k => newMap.push({ uuid: k.uuid, klasse: k }));


		return { ...state, klassenLoaded: true, klassenMap: newMap, loading: false };
	})

);


export function reducer(state: KlassenState | undefined, action: Action) {
	return klassenReducer(state, action);
};

