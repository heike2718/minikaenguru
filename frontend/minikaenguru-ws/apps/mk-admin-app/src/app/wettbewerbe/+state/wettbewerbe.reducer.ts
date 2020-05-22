import { Action, createReducer, on } from '@ngrx/store';
import * as WettbewerbeActions from './wettbewerbe.actions';
import { Wettbewerb } from '../wettbewerbe.model';


export const wettbewerbeFeatureKey = 'mk-admin-app-wettbewerbe';

export interface WettbewerbeState {
	wettbewerbe: Wettbewerb[];
	selectedWettbewerb: Wettbewerb;
	wettbewerbeLoaded: boolean;
};

export const initialWettbewerbeState = {
	wettbewerbe: [],
	selectedWettbewerb: undefined,
	wettbewerbeLoaded: false
} as WettbewerbeState;

const wettbewerbeReducer = createReducer(initialWettbewerbeState,

	on(WettbewerbeActions.allWettbewerbeLoaded, (state, action) => {
		return { ...state, wettbewerbe: action.wettbewerbe, selectedWettbewerb: undefined, wettbewerbeLoaded: true }
	}),
	on(WettbewerbeActions.selectWettbewerb, (state, action) => {
		return { ...state, selectedWettbewerb: action.wettbewerb }
	}),
	on (WettbewerbeActions.unselectWettbewerb, (state, _action) => {
		return {...state, selectedWettbewerb: undefined}
	})

);

export function reducer(state: WettbewerbeState | undefined, action: Action) {
	return wettbewerbeReducer(state, action);
}
