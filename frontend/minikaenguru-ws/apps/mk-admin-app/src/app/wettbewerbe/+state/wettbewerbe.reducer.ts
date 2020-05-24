import { Action, createReducer, on } from '@ngrx/store';
import * as WettbewerbeActions from './wettbewerbe.actions';
import { Wettbewerb, WettbewerbWithID } from '../wettbewerbe.model';

import { indexOfWettbewerbMitId } from '../wettbewerbe.model';


export const wettbewerbeFeatureKey = 'mk-admin-app-wettbewerbe';

export interface WettbewerbeState {
	wettbewerbeMap: WettbewerbWithID[],
	selectedJahr: number,
	wettbewerbeLoaded: boolean;
};

export const initialWettbewerbeState = {
	wettbewerbeMap: [],
	selectedJahr: undefined,
	wettbewerbeLoaded: false
} as WettbewerbeState;

const wettbewerbeReducer = createReducer(initialWettbewerbeState,

	on(WettbewerbeActions.allWettbewerbeLoaded, (state, action) => {

		const alle = action.wettbewerbe;
		const newMap = [];
		alle.forEach(w => newMap.push({ jahr: w.jahr, wettbewerb: w }));
		return { ...state, wettbewerbeMap: newMap, selectedJahr: undefined, wettbewerbeLoaded: true }
	}),

	on(WettbewerbeActions.selectWettbewerbsjahr, (state, action) => {
		const loaded = state.wettbewerbeMap.length > 0;
		return {...state, selectedJahr: action.jahr, wettbewerbeLoaded: loaded}
	}),

	on(WettbewerbeActions.selectedWettbewerbLoaded, (state, action) => {

		const loadedWettbewerb: Wettbewerb = action.wettbewerb;
		const jahr = loadedWettbewerb.jahr;
		const index = indexOfWettbewerbMitId(state.wettbewerbeMap, jahr);

		const loadedWettbewerbMitID: WettbewerbWithID = { jahr: jahr, wettbewerb: loadedWettbewerb };

		let neueMap: WettbewerbWithID[] = [];
		if (index >= 0) {
			state.wettbewerbeMap.forEach(w => {
				if (w.jahr !== jahr) {
					neueMap.push(w);
				} else {
					neueMap.push(loadedWettbewerbMitID);
				}
			});
		} else {
			neueMap = [...state.wettbewerbeMap, loadedWettbewerbMitID];
		}

		const loaded = state.wettbewerbeMap.length > 0;
		return { ...state, wettbewerbeMap: neueMap, selectedJahr: jahr, wettbewerbeLoaded: loaded }
	}),

);

export function reducer(state: WettbewerbeState | undefined, action: Action) {
	return wettbewerbeReducer(state, action);
}
