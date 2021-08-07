import { Action, createReducer, on } from '@ngrx/store';
import * as SchulteilnahmenActions from './schulteilnahmen.actions';
import { SchuleAdminOverviewWithID, SchuleAdminOverview, SchulenOverviewMap, replaceTeilnahme } from '../schulteilnahmen.model';
import { Teilnahme } from '@minikaenguru-ws/common-components';

export const schulteilnahmenFeatureKey = 'mk-admin-app-schulteilnahmen';

export interface SchulteilnahmenState {
	schulenMap: SchuleAdminOverviewWithID[],
	selectedSchule: SchuleAdminOverview,
	selectedTeilnahme: Teilnahme,
	fehlermeldungenUploadReport: string[];
	loading: boolean
};

const initialSchulteilnahmenState: SchulteilnahmenState = {
	schulenMap: [],
	selectedSchule: undefined,
	selectedTeilnahme: undefined,
	fehlermeldungenUploadReport: [],
	loading: false
};

const schulteilnahmenReducer = createReducer(initialSchulteilnahmenState,

	on(SchulteilnahmenActions.resetSchulteilnahmen, (_state, _action) => {

		return initialSchulteilnahmenState;

	}),

	on(SchulteilnahmenActions.schuleOverviewLoaded, (state, action) => {

		const neueSchulenMap = new SchulenOverviewMap(state.schulenMap).add(action.schuleAdminOverview);

		return {
			...state, loading: false, schulenMap: neueSchulenMap, selectedSchule: action.schuleAdminOverview
		};

	}),

	on(SchulteilnahmenActions.anonymisierteTeilnahmeSelected, (state, action) => {

		return {
			...state, selectedTeilnahme: action.teilnahme
		};
	}),

	on(SchulteilnahmenActions.auswertungImportert, (state, action) => {

		if (action.report) {

			const teilnahme = action.report.teilnahme;
			const neueTeilnahmen: Teilnahme[] = replaceTeilnahme(action.report.teilnahme, state.selectedSchule.schulteilnahmen);
			const neueSchule = { ...state.selectedSchule, schulteilnahmen: neueTeilnahmen };

			return {
				...state, selectedTeilnahme: teilnahme, selectedSchule: neueSchule, fehlermeldungenUploadReport: action.report.fehlerhafteZeilen
			};
		} else {
			return {...state, fehlermeldungenUploadReport: []};
		}
	}),

	on (SchulteilnahmenActions.dateiAusgewaehlt, (state, _action) => {

		return {...state, fehlermeldungenUploadReport: []};
	}),
);

export function reducer(state: SchulteilnahmenState | undefined, action: Action) {

	return schulteilnahmenReducer(state, action);
}


