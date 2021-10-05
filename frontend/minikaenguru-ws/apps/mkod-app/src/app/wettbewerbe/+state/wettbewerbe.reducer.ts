import { Action, createReducer, on } from '@ngrx/store';
import { Wettbewerb, WettbewerbWithID } from '../wettbewerb.model';
import * as WettbewerbeActions from './wettbewerbe.actions';

export const wettbewerbeFeatureKey = 'mkod-app-wettbewerbe';

export interface WettbewerbeState {
    readonly wettbewerbeMap: WettbewerbWithID[],
    readonly selectedWettbewerb?: Wettbewerb,
	readonly wettbewerbeLoaded: boolean;
    readonly loading: boolean;
}

const initialWettbewerbeState: WettbewerbeState = {
    wettbewerbeMap: [],
    selectedWettbewerb: undefined,
    wettbewerbeLoaded: false,
    loading: false
};

const wettbewerbeReducer = createReducer(initialWettbewerbeState,

    on(WettbewerbeActions.startLoadWettbewerbe, (state, _action) => {

		return { ...state, loading: true, wettbewerbeLoaded: false }
	}),

    on (WettbewerbeActions.wettbewerbeLoaded, (state, action) => {

        const alle = action.wettbewerbe;
		const newMap: WettbewerbWithID[] = [];
		alle.forEach(w => newMap.push({ jahr: w.jahr, wettbewerb: w }));

        return { ...state, loading: false, wettbewerbeLoaded: true, wettbewerbeMap: newMap, selectedWettbewerb: undefined};
    }),

    on(WettbewerbeActions.loadFinishedWithError, (state, _action) => {

        return { ...state, wettbewerbeLoaded: false, loading: false, selectedJahr: undefined};
    }),

    on (WettbewerbeActions.wettbewerbSelected, (state, action) => {

        return { ...state, selectedWettbewerb: action.wettbewerb};

    }),

    on(WettbewerbeActions.wettbewerbDeselected, (state, _action) => {

        return { ...state, selectedWettbewerb: undefined};
    }),

    on(WettbewerbeActions.reset, (_state, _action) => {
        return initialWettbewerbeState;
    })
);

export function reducer(state: WettbewerbeState | undefined, action: Action) {
	return wettbewerbeReducer(state, action);
};


