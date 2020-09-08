import { createReducer, Action, on } from '@ngrx/store';
import * as PrivatteilnahmenActions from './privatteilnahmen.actions';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';

export const privatteilnahmenFeatureKey = 'mkv-app-privatteilnahmen';

export interface PrivatteilnahmenState {
	readonly anonymisierteTeilnahmenGeladen: boolean;
	readonly anonymisierteTeilnahmen: AnonymisierteTeilnahme[];
	readonly loading: boolean;
}

const initialPrivatteilnahmenState: PrivatteilnahmenState = {
	anonymisierteTeilnahmenGeladen: false,
	anonymisierteTeilnahmen: [],
	loading: false
};


const privatteilnahmenReducer = createReducer(initialPrivatteilnahmenState,

	on(PrivatteilnahmenActions.startLoading, (state, _action) => {

		return { ...state, loading: true };
	}),


	on(PrivatteilnahmenActions.loadFinishedWithError, (state, _action) => {

		return { ...state, loading: false };
	}),


	on(PrivatteilnahmenActions.anonymeTeilnahmenGeladen, (state, action) => {

		return {
			...state,
			anonymisierteTeilnahmenGeladen: true,
			anonymisierteTeilnahmen: action.anonymeTeilnahmen,
			loading: false
		};
	}),

);

export function reducer(state: PrivatteilnahmenState | undefined, action: Action) {
	return privatteilnahmenReducer(state, action);
}




