import { Action, createReducer, on } from '@ngrx/store';
import { Message } from '@minikaenguru-ws/common-messages';
import { KatalogpflegeItem, Katalogpflegetyp, mergeKatalogItems, Kataloge } from '../katalogpflege.model';
import * as KatalogpflegeActions from './katalogpflege.actions';
import { act } from '@ngrx/effects';

export const katalogpflegeFeatureKey = 'mk-admin-app-kataloge';


export interface KatalogpflegeState {
	readonly kataloge: Kataloge;
	readonly selectedKatalogItem: KatalogpflegeItem;
	readonly selectedKatalogTyp: Katalogpflegetyp;
	readonly showLoadingIndicator: boolean;
	readonly sucheBeendet: boolean
}

const initialState: KatalogpflegeState = {
	kataloge: {laender: [], orte: [], schulen: []},
	selectedKatalogItem: undefined,
	selectedKatalogTyp: undefined,
	showLoadingIndicator: false,
	sucheBeendet: false
};

const katalogpflegeReducer = createReducer(initialState,

	on(KatalogpflegeActions.resetKataloge, (_state, _action) => {
		return initialState;
	}),

	on(KatalogpflegeActions.selectKatalogTyp, (state, action) => {

		return { ...state, selectedKatalogTyp: action.typ };

	}),

	on(KatalogpflegeActions.startSuche, (state, _action) => {

		return { ...state, showLoadingIndicator: true };
	}),

	on(KatalogpflegeActions.sucheFinished, (state, action) => {

		const alle: KatalogpflegeItem[] = action.katalogItems;
		const kataloge: Kataloge = mergeKatalogItems(alle, state.kataloge);
		return { ...state, kataloge: kataloge, showLoadingIndicator: false };
	}),

	on(KatalogpflegeActions.sucheFinishedWithError, (state, _action) => {
		return {...state, showLoadingIndicator: false};
	}),

	on(KatalogpflegeActions.selectKatalogItem, (state, action) => {
		return {...state, selectedKatalogItem: action.katalogItem};
	}),

	on(KatalogpflegeActions.resetSelection, (state, _action) => {
		return {...state, selectedKatalogItem: undefined, selectedKatalogTyp: undefined};
	})
);

export function reducer(state: KatalogpflegeState | undefined, action: Action) {
	return katalogpflegeReducer(state, action);
};

