import { Wettbewerb } from '../teilnahmen.model';
import { createReducer, Action, on } from '@ngrx/store';
import * as TeilnahmenActions from './teilnahmen.actions';

export const teilnahmenFeatureKey = 'mkv-app-teilnahmen';

export interface TeilnahmenState {
	readonly aktuellerWettbewerb: Wettbewerb;
	readonly hatZugangZuUnterlangen: boolean;
};

const initialTeilnahmenState: TeilnahmenState = {
	aktuellerWettbewerb: undefined,
	hatZugangZuUnterlangen: undefined
};


const teilnahmenReducer = createReducer(initialTeilnahmenState,

	on(TeilnahmenActions.aktuellerWettbewerbGeladen, (state, action) => {
		return { ...state, aktuellerWettbewerb: action.wettbewerb };
	}),

	on(TeilnahmenActions.zugangsstatusUnterlagenGeladen, (state, action) => {

		return { ...state, hatZugangZuUnterlangen: action.hatZugang };
	}),


	on(TeilnahmenActions.reset, (_state, _action) => {
		return initialTeilnahmenState;
	})
);

export function reducer(state: TeilnahmenState | undefined, action: Action) {
	return teilnahmenReducer(state, action);
}
