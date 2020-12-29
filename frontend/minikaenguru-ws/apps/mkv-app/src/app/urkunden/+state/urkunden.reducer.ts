import { createReducer, Action, on, State } from '@ngrx/store';
import { UrkundenauftragEinzelkind } from '../urkunden.model';
import * as UrkundenActions from './urkunden.actions';

export const urkundenFeatureKey = 'mkv-app-urkunden';

export interface UrkundenState {
	readonly loading: boolean;
	readonly urkundenauftragCompleted: boolean;
	readonly urkundenauftrag: UrkundenauftragEinzelkind;
};

const initialUrkundenState: UrkundenState = {
	loading: false,
	urkundenauftragCompleted: false,
	urkundenauftrag: undefined
};

export const urkundenReducer = createReducer(initialUrkundenState,

	on(UrkundenActions.initialerUrkundenauftragCreated, (state, action) => {
		return {...state, urkundenauftrag: action.auftrag};
	}),

	on(UrkundenActions.startLoading, (state, _action) => {

		return {...state, loading: true};
	}),

	on(UrkundenActions.finishedWithError, (state, _action) => {
		return {...state, loading: false};
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
