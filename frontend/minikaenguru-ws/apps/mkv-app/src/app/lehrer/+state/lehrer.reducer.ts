import { createReducer, Action, on } from '@ngrx/store';
import { SchuleWithID, Schule, mergeSchulenMap, findSchuleMitId, SchuleDetails } from './../schulen/schulen.model';
import * as LehrerActions from './lehrer.actions';
import { Schulteilnahme } from '../../wettbewerb/wettbewerb.model';
export const lehrerFeatureKey = 'mkv-app-lehrer';

export interface LehrerState {
	readonly hatZugangZuUnterlangen: boolean;
	readonly schulen: SchuleWithID[];
	readonly selectedSchule: Schule;
	readonly schulenLoaded: boolean;
	readonly loading: boolean;

};

const initalLehrerState: LehrerState = {
	hatZugangZuUnterlangen: undefined,
	schulen: [],
	selectedSchule: undefined,
	schulenLoaded: false,
	loading: false
};

const lehrerReducer = createReducer(initalLehrerState,

	on(LehrerActions.zugangsstatusUnterlagenGeladen, (state, action) => {
		return { ...state, hatZugangZuUnterlangen: action.hatZugang, loading: false }
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

		const neueDetails: SchuleDetails = {...alteDetails, angemeldetDurch: action.angemeldetDurch};
		const neueSchule: Schule = { ...alteSchule, aktuellAngemeldet: true, details: neueDetails };
		const neueMap = mergeSchulenMap(state.schulen, neueSchule);
		const neuerState = {...state, schulen: neueMap, selectedSchule: neueSchule};
		return neuerState;
	}),

	on(LehrerActions.restoreDetailsFromCache, (state, action) => {

		const schule = findSchuleMitId(state.schulen, action.kuerzel);

		if (schule != null) {
			return { ...state, selectedSchule: schule };
		}

		return state;
	}),

	on(LehrerActions.deselectSchule, (state, _action) => {
		return { ...state, selectedSchule: undefined }
	}),

	on(LehrerActions.resetLehrer, (_state, _action) => {
		return initalLehrerState;
	})

);


export function reducer(state: LehrerState | undefined, action: Action) {

	return lehrerReducer(state, action);

};

