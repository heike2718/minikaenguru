import { Action, createReducer, on } from '@ngrx/store';
import * as VeranstalterActions from './veranstalter.actions';
import { VeranstalterWithID, VeranstalterMap, Veranstalter } from '../veranstalter.model';

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

	on(VeranstalterActions.sucheFinishedWithError, (_state, _action) => {
		return initialVeranstalterState;
	}),

	on(VeranstalterActions.veranstalterSelected, (state, action) => {


		return { ...state, selectedVeranstalter: action.veranstalter };

	}),

	on(VeranstalterActions.startLoadDetails, (state, _action) => {
		return { ...state, loading: true };
	}),

	on(VeranstalterActions.loadDetailsFinishedWithError, (state, _action) => {
		return { ...state, loading: false };
	}),

	on(VeranstalterActions.privatteilnahmeOverviewLoaded, (state, action) => {

		const veranstalterMapAktuell = new VeranstalterMap(state.veranstalterMap);
		const newSelectedVeranstalter = { ...state.selectedVeranstalter, schuleAdminOverview: undefined, privatOverview: action.privatteilnahmeOverview };
		const veranstalterArray: Veranstalter[] = [];
		veranstalterArray.push(newSelectedVeranstalter);
		const neueVeranstalterMap = veranstalterMapAktuell.merge(veranstalterArray);

		return {
			...state, loading: false, sucheFinished: true, veranstalterMap: neueVeranstalterMap
			, selectedVeranstalter: newSelectedVeranstalter
		};

	}),

	on(VeranstalterActions.zugangsstatusUnterlagenGeaendert, (state, action) => {

		const theSelectedVeranstalter: Veranstalter = state.selectedVeranstalter;

		if (theSelectedVeranstalter && theSelectedVeranstalter.uuid === action.veranstalter.uuid) {

			if (theSelectedVeranstalter.zugangsstatusUnterlagen !== action.neuerStatus) {

				const changedVeranstalter: Veranstalter = { ...theSelectedVeranstalter, zugangsstatusUnterlagen: action.neuerStatus };


				const veranstalterMapAktuell = new VeranstalterMap(state.veranstalterMap);
				const veranstalterArray: Veranstalter[] = [];
				veranstalterArray.push(changedVeranstalter);
				const neueVeranstalterMap = veranstalterMapAktuell.merge(veranstalterArray);

				return { ...state, veranstalterMap: neueVeranstalterMap, selectedVeranstalter: changedVeranstalter, loading: false };
			}

		}

		return { ...state, loading: false };

	}),

	on(VeranstalterActions.newsletterDeaktiviert, (state, action) => {

		const theSelectedVeranstalter: Veranstalter = state.selectedVeranstalter;

		if (theSelectedVeranstalter && theSelectedVeranstalter.uuid === action.veranstalter.uuid) {

			if (theSelectedVeranstalter.newsletterAbonniert) {

				const changedVeranstalter: Veranstalter = { ...theSelectedVeranstalter, newsletterAbonniert: false };


				const veranstalterMapAktuell = new VeranstalterMap(state.veranstalterMap);
				const veranstalterArray: Veranstalter[] = [];
				veranstalterArray.push(changedVeranstalter);
				const neueVeranstalterMap = veranstalterMapAktuell.merge(veranstalterArray);

				return { ...state, veranstalterMap: neueVeranstalterMap, selectedVeranstalter: changedVeranstalter, loading: false };
			}

		}

		return { ...state, loading: false };

	})
);

export function reducer(state: VeranstalterState | undefined, action: Action) {

	return veranstalterReducer(state, action);
};




