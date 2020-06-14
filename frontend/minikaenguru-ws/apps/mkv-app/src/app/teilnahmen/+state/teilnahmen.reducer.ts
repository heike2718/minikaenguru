import { Wettbewerb, ZugangUnterlagenStatus } from '../teilnahmen.model';
import { createReducer, Action, on } from '@ngrx/store';
import * as TeilnahmenActions from './teilnahmen.actions';

export const teilnahmenFeatureKey = 'mkv-app-teilnahmen';

export interface TeilnahmenState {
	readonly aktuellerWettbewerb: Wettbewerb;
	readonly zugangUnterlagen: ZugangUnterlagenStatus;
	readonly hatZugangZuUnterlangen: boolean;
};

const initialTeilnahmenState: TeilnahmenState = {
	aktuellerWettbewerb: undefined,
	zugangUnterlagen: undefined,
	hatZugangZuUnterlangen: false
};


const teilnahmenReducer = createReducer(initialTeilnahmenState,

	on(TeilnahmenActions.aktuellerWettbewerbGeladen, (state, action) => {
		return { ...state, aktuellerWettbewerb: action.wettbewerb };
	}),

	on(TeilnahmenActions.reset, (_state, _action) => {
		return initialTeilnahmenState;
	})
);

export function reducer(state: TeilnahmenState | undefined, action: Action) {
	return teilnahmenReducer(state, action);
}
