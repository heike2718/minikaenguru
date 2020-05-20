import { Action, createReducer, on, State } from '@ngrx/store';
import * as VeranstalterActions from './veranstalter.actions';

export const veranstalterFeatureKey = 'mkv-app-veranstalter';

export interface VeranstalterState {
};

export const initialVeranstalterState: VeranstalterState = {
	teilnahmenummern: undefined,
	teilnahmenummernLoaded: false
};

const veranstalterReducer = createReducer(initialVeranstalterState,

	on(VeranstalterActions.allTeilnahmenummernLoaded, (state, action) => {

		return { ...state, teilnahmenummern: action.teilnahmenummern, teilnahmenummernLoaded: true }

	}),
);



export function reducer(state: VeranstalterState | undefined, action: Action) {
	return veranstalterReducer(state, action);


};

