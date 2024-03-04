import { createReducer, Action, on } from '@ngrx/store';
import { SchuleWithID, Schule, mergeSchulenMap, findSchuleMitId, SchuleDetails, SchulenMap, compareSchulen } from './../schulen/schulen.model';
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
	readonly lehrer?: Lehrer;
	readonly schulen: SchuleWithID[];
	readonly selectedSchule?: Schule;
	readonly schulenLoaded: boolean;
	readonly addSchuleState: AddSchuleState;

};

const initalLehrerState: LehrerState = {
	lehrer: undefined,
	schulen: [],
	selectedSchule: undefined,
	schulenLoaded: false,
	addSchuleState: initialAddSchuleState
};

const lehrerReducer = createReducer(initalLehrerState,

	on(LehrerActions.datenLehrerGeladen, (state, action) => {
		return { ...state, lehrer: action.lehrer }
	}),

	on(LehrerActions.aboNewsletterChanged, (state, _action) => {

		if (state.lehrer) {
			const abonniert = !state.lehrer.newsletterAbonniert;
			const lehrer = { ...state.lehrer, newsletterAbonniert: abonniert };
			return { ...state, lehrer: lehrer };
		}

		return { ...state };
	}),

	on(LehrerActions.finishedWithError, (state, _action) => {
		return { ...state };
	}),

	on(LehrerActions.schulenLoaded, (state, action) => {

		const schulen: Schule[] = [...action.schulen];
		schulen.sort((s1, s2) => compareSchulen(s1, s2)); const newMap: SchuleWithID[] = [];

		schulen.forEach(s => newMap.push({ kuerzel: s.kuerzel, schule: s }));
		return { ...state, schulen: newMap, selectedSchule: undefined, schulenLoaded: true }
	}),

	on(LehrerActions.selectSchule, (state, action) => {
		return { ...state, selectedSchule: action.schule }
	}),

	on(LehrerActions.schuleDetailsLoaded, (state, action) => {

		const neueMap = mergeSchulenMap(state.schulen, action.schule);
		return { ...state, selectedSchule: action.schule, schulen: neueMap };
	}),

	on(LehrerActions.schuleAngemeldet, (state, action) => {

		// TODO: wird noch nicht benötigt, da es noch nicht im schule.state eingebaut wurde
		const schulteilnahme: Schulteilnahme = action.teilnahme;

		const alteSchule = state.selectedSchule;

		if (alteSchule) {
			const alteDetails = alteSchule.details;

			if (alteDetails) {
				const anzahlTeilnahmen = alteDetails.anzahlTeilnahmen + 1;
				const neueDetails: SchuleDetails = {
					...alteDetails
					, angemeldetDurch: action.angemeldetDurch
					, anzahlTeilnahmen: anzahlTeilnahmen
				};
				const neueSchule: Schule = { ...alteSchule, aktuellAngemeldet: true, details: neueDetails, auswertungsmodus: 'INDIFFERENT' };
				const neueMap = mergeSchulenMap(state.schulen, neueSchule);
				return { ...state, schulen: neueMap, selectedSchule: neueSchule };
			}
		}

		return { ...state };

	}),

	on(LehrerActions.auswertungstabelleHochgeladen, (state, action) => {

		const alteSchule = state.selectedSchule;
		let neueSchule: Schule;

		if (alteSchule) {
			neueSchule = { ...alteSchule, auswertungsmodus: 'OFFLINE' };
		} else {
			neueSchule = { ...action.schule, auswertungsmodus: 'OFFLINE' };
		}

		const neueMap = mergeSchulenMap(state.schulen, neueSchule);
		return { ...state, schulen: neueMap, selectedSchule: neueSchule };
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
		const selectedSchule = state.selectedSchule;

		if (schule && selectedSchule && schule.details) {

			const changedSchuleDetails = { ...schule.details, hatAdv: true };
			const changedSchule = { ...selectedSchule, details: changedSchuleDetails };

			if (changedSchule) {
				const neueSchuleMap = mergeSchulenMap(state.schulen, changedSchule);

				return { ...state, schulen: neueSchuleMap, selectedSchule: changedSchule };
			}

		}

		return { ...state };
	}),

	on(LehrerActions.schuleAdded, (state, action) => {

		const schule = action.schule;
		if (state.lehrer) {
			const neueTeilnahmenummern = [...state.lehrer.teilnahmenummern];
			neueTeilnahmenummern.push(schule.kuerzel);

			const neueMap = new SchulenMap(state.schulen).merge(schule);

			return {
				...state,
				schulen: neueMap,
				addSchuleState: initialAddSchuleState,
				loading: false,
				schulenLoaded: true,
				selectedSchule: undefined,
				lehrer: { ...state.lehrer, teilnahmenummern: neueTeilnahmenummern }
			};
		}

		return { ...state };
	}),

	on(LehrerActions.schuleRemoved, (state, action) => {

		const neueMap = new SchulenMap(state.schulen).remove(action.kuerzel);

		if (state.lehrer) {

			const neueTeilnahmenummern = [];

			for (let t of state.lehrer.teilnahmenummern) {

				if (action.kuerzel !== t) {
					neueTeilnahmenummern.push(t);
				}

			}
			return {
				...state, selectedSchule: undefined, schulenLoaded: false, addSchuleState: initialAddSchuleState, schulen: neueMap
				, lehrer: { ...state.lehrer, teilnahmenummern: neueTeilnahmenummern }
			};
		}

		return { ...state };
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


		if (selectedItem) {


			const schuleBereitsZugeordnet = new SchulenMap(state.schulen).has(selectedItem.kuerzel);

			if (schuleBereitsZugeordnet) {
				const neuerAddSchuleState: AddSchuleState = {
					...state.addSchuleState,
					showSchulkatalog: false,
					btnAddMeToSchuleDisabled: true,
					showTextSchuleBereitsZugeordnet: schuleBereitsZugeordnet
				}

				return { ...state, addSchuleState: neuerAddSchuleState };
			} else {
				const neuerAddSchuleState: AddSchuleState = {
					...state.addSchuleState,
					showSchulkatalog: false,
					btnAddMeToSchuleDisabled: false,
					showTextSchuleBereitsZugeordnet: false
				}

				return { ...state, addSchuleState: neuerAddSchuleState };
			}

		}

		return { ...state };

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

