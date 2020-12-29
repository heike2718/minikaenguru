import { createReducer, Action, on, State } from '@ngrx/store';
import * as UrkundenActions from './urkunden.actions';

export const urkundenFeatureKey = 'mkv-app-urkunden';

export interface UrkundenState {
	readonly loading: boolean;
};

const initialUrkundenState: UrkundenState = {
	loading: false
};

export const urkundenReducer = createReducer(initialUrkundenState,

	on(UrkundenActions.startLoading, (state, _action) => {

		return {...state, loading: true};
	}),

	on(UrkundenActions.downloadFinished, (state, _action) => {

		return {...state, loading: false};
	}),

	on(UrkundenActions.resetModule, (_state, _action) => {
		return initialUrkundenState;
	})

);


export function reducer(state: UrkundenState | undefined, action: Action) {
	return urkundenReducer(state, action);
};
