import { createReducer, Action, on } from '@ngrx/store';
import * as WettbewerbActions from './wettbewerb.actions';
import { Wettbewerb } from '../wettbewerb.model';

export const wettbewerbFeatureKey = 'mkv-app-wettbewerb';

export interface WettbewerbState {
	readonly aktuellerWettbewerb: Wettbewerb;
};

const initialWettbewerbState: WettbewerbState = {
	aktuellerWettbewerb: undefined
};


const wettbewerbReducer = createReducer(initialWettbewerbState,

	on(WettbewerbActions.aktuellerWettbewerbGeladen, (state, action) => {
		return { ...state, aktuellerWettbewerb: action.wettbewerb };
	}),


	on(WettbewerbActions.reset, (_state, _action) => {
		return initialWettbewerbState;
	})
);

export function reducer(state: WettbewerbState | undefined, action: Action) {
	return wettbewerbReducer(state, action);
}
