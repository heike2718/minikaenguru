import { Action, createReducer, on } from '@ngrx/store';
import * as SchulteilnahmenActions from './schulteilnahmen.actions';
import { SchuleAdminOverviewWithID, SchuleAdminOverview, SchulenOverviewMap, replaceTeilnahme } from '../schulteilnahmen.model';
import { Teilnahme } from '@minikaenguru-ws/common-components';

export const schulteilnahmenFeatureKey = 'mk-admin-app-schulteilnahmen';

export interface SchulteilnahmenState {
	schulenMap: SchuleAdminOverviewWithID[],
	selectedSchule?: SchuleAdminOverview,
	selectedTeilnahme?: Teilnahme,
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

		if (action.schuleAdminOverview) {
			const neueSchulenMap = new SchulenOverviewMap(state.schulenMap).add(action.schuleAdminOverview);
			return {...state, loading: false, schulenMap: neueSchulenMap, selectedSchule: action.schuleAdminOverview};
		}

		return {...state};

	}),

	on(SchulteilnahmenActions.anonymisierteTeilnahmeSelected, (state, action) => {

		return {
			...state, selectedTeilnahme: action.teilnahme
		};
	}),

	on(SchulteilnahmenActions.auswertungImportiert, (state, action) => {

		if (state.selectedSchule) {
			const neueTeilnahmen = replaceTeilnahme(action.report.teilnahme, state.selectedSchule.schulteilnahmen);
			const neueSchule: SchuleAdminOverview = { ...state.selectedSchule, schulteilnahmen: neueTeilnahmen };
			return {...state, selectedTeilnahme: action.report.teilnahme, selectedSchule: neueSchule, fehlermeldungenUploadReport: action.report.fehlerhafteZeilen};
		}	
		
		return {...state};
	}),

	on (SchulteilnahmenActions.dateiAusgewaehlt, (state, _action) => {

		return {...state, fehlermeldungenUploadReport: []};
	}),
);

export function reducer(state: SchulteilnahmenState | undefined, action: Action) {

	return schulteilnahmenReducer(state, action);
}


