import { createReducer, Action, on } from '@ngrx/store';
import * as WettbewerbActions from './wettbewerb.actions';
import { Wettbewerb, AbstractVeranstalter } from '../wettbewerb.model';

export const wettbewerbFeatureKey = 'mkv-app-wettbewerb';

export interface WettbewerbState {
	readonly aktuellerWettbewerb: Wettbewerb;
	readonly veranstalter: AbstractVeranstalter;
};

const initialWettbewerbState: WettbewerbState = {
	aktuellerWettbewerb: undefined,
	veranstalter: undefined
};


const wettbewerbReducer = createReducer(initialWettbewerbState,

	on(WettbewerbActions.aktuellerWettbewerbGeladen, (state, action) => {
		return { ...state, aktuellerWettbewerb: action.wettbewerb };
	}),


	on(WettbewerbActions.reset, (_state, _action) => {
		return initialWettbewerbState;
	}),

	on(WettbewerbActions.veranstalterLoaded, (state, action) => {
		return { ...state, veranstalter: action.veranstalter }
	}),

);

export function reducer(state: WettbewerbState | undefined, action: Action) {
	return wettbewerbReducer(state, action);
}
