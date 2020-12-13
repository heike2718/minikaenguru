import { createReducer, Action, on } from '@ngrx/store';
import { SchuleWithID, Schule, mergeSchulenMap, findSchuleMitId, SchuleDetails, SchulenMap } from './../schulen/schulen.model';
import * as LehrerActions from './lehrer.actions';
import { Schulteilnahme, Lehrer } from '../../wettbewerb/wettbewerb.model';
export const lehrerFeatureKey = 'mkv-app-lehrer';

export interface AddSchuleState {
	readonly showSchulkatalog: boolean;
	readonly showTextSchuleBereitsZugeordnet: boolean;
	readonly btnAddMeToSchuleDisabled: boolean;
}

const initialAddSchuleState: AddSchuleState = {
	showSchulkatalog: false,
	showTextSchuleBereitsZugeordnet: false,
	btnAddMeToSchuleDisabled: true,
}

export interface LehrerState {
	readonly lehrer: Lehrer;
	readonly schulen: SchuleWithID[];
	readonly selectedSchule: Schule;
	readonly schulenLoaded: boolean;
	readonly addSchuleState: AddSchuleState;
	readonly loading: boolean;

};

const initalLehrerState: LehrerState = {
	lehrer: undefined,
	schulen: [],
	selectedSchule: undefined,
	schulenLoaded: false,
	addSchuleState: initialAddSchuleState,
	loading: false
};

const lehrerReducer = createReducer(initalLehrerState,

	on(LehrerActions.datenLehrerGeladen, (state, action) => {
		return { ...state, lehrer: action.lehrer, loading: false }
	}),

	on(LehrerActions.aboNewsletterChanged, (state, _action) => {

		const abonniert = !state.lehrer.newsletterAbonniert;
		const lehrer = { ...state.lehrer, newsletterAbonniert: abonniert };
		return { ...state, loading: false, lehrer: lehrer };
	}),

	on(LehrerActions.startLoading, (state, _action) => {
		return { ...state, loading: true };
	}),

	on(LehrerActions.finishedWithError, (state, _action) => {
		return { ...state, loading: false };
	}),

	on(LehrerActions.schulenLoaded, (state, action) => {

		const schulen: Schule[] = action.schulen;
		const newMap: SchuleWithID[] = [];
		schulen.forEach(s => newMap.push({ kuerzel: s.kuerzel, schule: s }));

		return { ...state, schulen: newMap, selectedSchule: undefined, schulenLoaded: true, loading: false }
	}),

	on(LehrerActions.selectSchule, (state, action) => {
		return { ...state, selectedSchule: action.schule }
	}),

	on(LehrerActions.schuleDetailsLoaded, (state, action) => {

		const neueMap = mergeSchulenMap(state.schulen, action.schule);
		return { ...state, selectedSchule: action.schule, schulen: neueMap, loading: false };
	}),

	on(LehrerActions.schuleAngemeldet, (state, action) => {

		// TODO: wird noch nicht benötigt, da es noch nicht im schule.state eingebaut wurde
		const schulteilnahme: Schulteilnahme = action.teilnahme;

		const alteSchule = state.selectedSchule;
		const alteDetails = alteSchule.details;

		const anzahlTeilnahmen = alteDetails.anzahlTeilnahmen + 1;
		const neueDetails: SchuleDetails = { ...alteDetails, angemeldetDurch: action.angemeldetDurch, anzahlTeilnahmen: anzahlTeilnahmen };
		const neueSchule: Schule = { ...alteSchule, aktuellAngemeldet: true, details: neueDetails };
		const neueMap = mergeSchulenMap(state.schulen, neueSchule);
		const neuerState = { ...state, schulen: neueMap, selectedSchule: neueSchule };
		return neuerState;
	}),

	on(LehrerActions.restoreDetailsFromCache, (state, action) => {

		const schule = findSchuleMitId(state.schulen, action.kuerzel);

		if (schule != null) {
			return { ...state, selectedSchule: schule, addSchuleState: initialAddSchuleState };
		}

		return state;
	}),

	on(LehrerActions.vertragAdvCreated, (state, action) => {

		const schule = findSchuleMitId(state.schulen, action.schulkuerzel);

		if (schule != null) {

			const changedSchuleDetails = { ...schule.details, hatAdv: true };
			const changedSchule = { ...state.selectedSchule, details: changedSchuleDetails };
			const neueSchuleMap = mergeSchulenMap(state.schulen, changedSchule);

			return { ...state, schulen: neueSchuleMap, selectedSchule: changedSchule };
		}

		return state;
	}),

	on(LehrerActions.schuleAdded, (state, action) => {

		const schule = action.schule;

		const neueMap = new SchulenMap(state.schulen).merge(schule);

		return {
			...state,
			schulen: neueMap,
			addSchuleState: initialAddSchuleState,
			loading: false,
			schulenLoaded: true,
			selectedSchule: undefined
		};
	}),

	on(LehrerActions.schuleRemoved, (state, action) => {

		const neueMap = new SchulenMap(state.schulen).remove(action.kuerzel);

		return { ...state, selectedSchule: undefined, addSchuleState: initialAddSchuleState, schulen: neueMap };
	}),

	on(LehrerActions.schulkatalogEinblenden, (state, _action) => {

		const neuerAddSchuleState: AddSchuleState = {
			showSchulkatalog: true,
			showTextSchuleBereitsZugeordnet: false,
			btnAddMeToSchuleDisabled: true
		}

		return {
			...state,
			addSchuleState: neuerAddSchuleState,
			loading: false,
			schulenLoaded: true,
			selectedSchule: undefined
		};
	}),

	on(LehrerActions.neueSchuleSelected, (state, action) => {

		const selectedItem = action.selectedKatalogItem;


		if (!selectedItem) {
			return {
				...state, addSchuleState: {
					showSchulkatalog: false,
					showTextSchuleBereitsZugeordnet: false,
					btnAddMeToSchuleDisabled: true
				}
			};
		}

		const schuleBereitsZugeordnet = new SchulenMap(state.schulen).has(selectedItem.kuerzel);

		const neuerAddSchuleState: AddSchuleState = { ...state.addSchuleState,
			btnAddMeToSchuleDisabled: schuleBereitsZugeordnet,
			showTextSchuleBereitsZugeordnet: schuleBereitsZugeordnet }

		return { ...state, addSchuleState: neuerAddSchuleState };
	}),

	on(LehrerActions.schuleAdded, (state, action) => {

		const schule = action.schule;

		const neueMap = new SchulenMap(state.schulen).merge(schule);

		return {
			...state,
			schulen: neueMap,
			addSchuleState: initialAddSchuleState,
			loading: false,
			schulenLoaded: true,
			selectedSchule: undefined
		};
	}),

	on(LehrerActions.closeSchulsuche, (state, _action) => {

		return {
			...state,
			addSchuleState: initialAddSchuleState,
			loading: false,
			schulenLoaded: true,
			selectedSchule: undefined
		};
	}),

	on(LehrerActions.resetLehrer, (_state, _action) => {
		return initalLehrerState;
	})

);


export function reducer(state: LehrerState | undefined, action: Action) {

	return lehrerReducer(state, action);

};

