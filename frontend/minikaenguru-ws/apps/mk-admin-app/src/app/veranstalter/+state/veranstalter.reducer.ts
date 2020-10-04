import { Action, createReducer, on } from '@ngrx/store';
import * as VeranstalterActions from './veranstalter.actions';
import { VeranstalterWithID, VeranstalterMap, Veranstalter } from '../veranstalter.model';
import { asapScheduler } from 'rxjs';

export const veranstalterFeatureKey = 'mk-admin-app-veranstalter';

export interface VeranstalterState {
	veranstalterMap: VeranstalterWithID[],
	selectedVeranstalter: Veranstalter,
	sucheFinished: boolean,
	loading: boolean
};

const initialVeranstalterState: VeranstalterState = {
	veranstalterMap: [],
	selectedVeranstalter: undefined,
	sucheFinished: false,
	loading: false
};

const veranstalterReducer = createReducer(initialVeranstalterState,

	on(VeranstalterActions.resetVeranstalters, (_state, _action) => {


		return initialVeranstalterState;

	}),

	on(VeranstalterActions.startSuche, (state, _action) => {

		return { ...state, loading: true, selectedVeranstalter: undefined };

	}),

	on(VeranstalterActions.sucheFinished, (state, action) => {

		const trefferliste = action.veranstalter;

		const neueVeranstalterMap = new VeranstalterMap(state.veranstalterMap).add(trefferliste);

		return { ...state, loading: false, sucheFinished: true, selectedVeranstalter: undefined, veranstalterMap: neueVeranstalterMap };

	}),

	on(VeranstalterActions.veranstalterSelected, (state, action) => {


		return { ...state, selectedVeranstalter: action.veranstalter };

	}),

	on(VeranstalterActions.selectedVeranstalterLoaded, (state, action) => {

		const added: Veranstalter[] = [];
		added.push(action.veranstalter);
		const neueMap: VeranstalterWithID[] = new VeranstalterMap(state.veranstalterMap).add(added);
		return { ...state, loading: false, selectedVeranstalter: action.veranstalter, sucheFinished: true, veranstalterMap: neueMap };

	})
);

export function reducer(state: VeranstalterState | undefined, action: Action) {

	return veranstalterReducer(state, action);
};




