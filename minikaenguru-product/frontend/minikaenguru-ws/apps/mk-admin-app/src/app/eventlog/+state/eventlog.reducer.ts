import { Action, createReducer, on } from '@ngrx/store';
import * as EventlogActions from './eventlog.actions';

export const eventlogFeatureKey = 'mka-eventlog';

export interface EventlogState {
	readonly datum?: string;
};

const initialEventlogState: EventlogState = {
	datum: undefined
};


export const eventlogReducer = createReducer(initialEventlogState,

	on(EventlogActions.dateSubmitted, (state, action) => {

		return { ...state, datum: action.datum };

	}),

	on(EventlogActions.dateCleared, (state, _action) => {

		return { ...state, datum: undefined };
	})


);

export function reducer(state: EventlogState | undefined, action: Action) {
	return eventlogReducer(state, action);
}

