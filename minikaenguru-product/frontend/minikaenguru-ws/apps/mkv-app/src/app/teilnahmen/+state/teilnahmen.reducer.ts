import { createReducer, Action, on } from '@ngrx/store';
import * as TeilnahmenActions from './teilnahmen.actions';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';


export const teilnahmenFeatureKey = 'mkv-app-teilnahmen';

export interface TeilnahmenummerAndName {
	readonly teilnahmenummer: string,
	readonly nameSchule: string
}

export interface TeilnahmenState {
	readonly teilnahmenummerAndName?: TeilnahmenummerAndName;
	readonly anonymisierteTeilnahmenGeladen: boolean;
	readonly anonymisierteTeilnahmen: AnonymisierteTeilnahme[];
	readonly loading: boolean;
}

const initialTeilnahmenState: TeilnahmenState = {
	teilnahmenummerAndName: undefined,
	anonymisierteTeilnahmenGeladen: false,
	anonymisierteTeilnahmen: [],
	loading: false
};

const teilnahmenReducer = createReducer(initialTeilnahmenState,



	on(TeilnahmenActions.teilnahmenummerSelected, (state, action) => {

		const nummerUndName: TeilnahmenummerAndName = { teilnahmenummer: action.teilnahmenummer, nameSchule: action.nameSchule };

		return { ...state, teilnahmenummerAndName: nummerUndName };
	}),


	on(TeilnahmenActions.startLoading, (state, _action) => {

		return { ...state, loading: true };
	}),

	on(TeilnahmenActions.loadFinishedWithError, (state, _action) => {

		return { ...state, loading: false };
	}),


	on(TeilnahmenActions.anonymeTeilnahmenGeladen, (state, action) => {

		return {
			...state,
			anonymisierteTeilnahmenGeladen: true,
			anonymisierteTeilnahmen: action.anonymeTeilnahmen,
			loading: false
		};
	}),

	on(TeilnahmenActions.resetState, (_state, _action) => {

		return initialTeilnahmenState;
	})
);

export function reducer(state: TeilnahmenState | undefined, action: Action) {
	return teilnahmenReducer(state, action);
}




