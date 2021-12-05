import { createReducer, Action, on } from '@ngrx/store';
import * as KlassenActions from './klassen.actions';
import { KlasseWithID, KlassenMap, KlassenlisteImportReport, KlasseUIModel } from '../klassen.model';
import { KlasseEditorModel, Klasse, Kind } from '@minikaenguru-ws/common-components';


export const klassenFeatureKey = 'mkv-app-klassen';

export interface KlassenState {
	klassenMap: KlasseWithID[];
	klassenLoaded: boolean;
	loading: boolean;
	klasseUIModel?: KlasseUIModel;
	selectedKlasse?: Klasse;
	importReport?: KlassenlisteImportReport;
};

const initialKlassenState: KlassenState = {
	klassenMap: [],
	klassenLoaded: false,
	loading: false,
	klasseUIModel: undefined,
	selectedKlasse: undefined,
	importReport: undefined
};

const klassenReducer = createReducer(initialKlassenState,

	on(KlassenActions.resetModule, (_state, _action) => {
		return initialKlassenState;
	}),

	on(KlassenActions.allKlassenLoaded, (state, action) => {

		const alle = action.klassen;
		const newMap: KlasseWithID[] = [];
		alle.forEach(k => newMap.push({ uuid: k.uuid, klasse: k }));


		return { ...state, klassenLoaded: true, klassenMap: newMap, loading: false };
	}),

	on(KlassenActions.createNewKlasse, (state, _action) => {

		const klasseUIModel: KlasseUIModel = {uuid: 'neu', name: '', saved: false};
		return { ...state, klasseUIModel: klasseUIModel };
	}),

	on(KlassenActions.startEditingKlasse, (state, action) => {

		const klasse = action.klasse;

		return { ...state, selectedKlasse: klasse, klasseUIModel: {name:klasse.name, uuid: klasse.uuid, saved: false} };

	}),

	on(KlassenActions.startAssigningKinder, (state, action) => {

		const klasse = action.klasse;
		return { ...state, editorModel: undefined, selectedKlasse: klasse };

	}),

	on(KlassenActions.kindAdded, (state, _action) => {

		if (!state.selectedKlasse) {
			return {...state};
		}

		const anzahlKinder = state.selectedKlasse.anzahlKinder + 1;
		const selectedKlasse = { ...state.selectedKlasse, anzahlKinder };
		const merged: KlasseWithID[] = new KlassenMap(state.klassenMap).merge(selectedKlasse);

		return { ...state, selectedKlasse: selectedKlasse, klassenMap: merged };

	}),

	on(KlassenActions.kindDeleted, (state, action) => {

		if (!state.selectedKlasse) {
			return {...state};
		}

		const anzahlKinder = state.selectedKlasse.anzahlKinder - 1;
		let anzahlLoesungszettel = state.selectedKlasse.anzahlLoesungszettel;
		if (action.kind && action.kind.punkte) {
			anzahlLoesungszettel = anzahlLoesungszettel - 1;
		}
		const selectedKlasse = { ...state.selectedKlasse, anzahlKinder: anzahlKinder, anzahlLoesungszettel: anzahlLoesungszettel };
		const merged: KlasseWithID[] = new KlassenMap(state.klassenMap).merge(selectedKlasse);

		return { ...state, selectedKlasse: selectedKlasse, klassenMap: merged };

	}),

	on(KlassenActions.loesungszettelAdded, (state, action) => {

		const kind: Kind = action.kind;

		if (!state.selectedKlasse || !kind.klasseId) {
			return {...state};
		}

		
		const klasseID = kind.klasseId;
		if (state.selectedKlasse.uuid === klasseID) {
			const anzahlLoesungszettel = state.selectedKlasse.anzahlLoesungszettel + 1;
			const neueGewaehlteKlasse = {...state.selectedKlasse, anzahlLoesungszettel: anzahlLoesungszettel};

			const neueMap = new KlassenMap(state.klassenMap).merge(neueGewaehlteKlasse);

			return {...state, selectedKlasse: neueGewaehlteKlasse, klassenMap: neueMap};
		} else {
			return {...state};
		}

	}),

	on(KlassenActions.loesungszettelDeleted, (state, action) => {

		const kind: Kind = action.kind;
		const klasseID = kind.klasseId;

		if (!state.selectedKlasse || !klasseID ) {
			return {...state};
		}

		if (state.selectedKlasse && state.selectedKlasse.uuid === klasseID) {
			const anzahlLoesungszettel = state.selectedKlasse.anzahlLoesungszettel - 1;
			const neueGewaehlteKlasse = {...state.selectedKlasse, anzahlLoesungszettel: anzahlLoesungszettel};

			const neueMap = new KlassenMap(state.klassenMap).merge(neueGewaehlteKlasse);

			return {...state, selectedKlasse: neueGewaehlteKlasse, klassenMap: neueMap};
		} else {
			return {...state};
		}
	}),

	on(KlassenActions.klasseSaved, (state, action) => {

		const klasse = action.klasse;

		const neueMap = new KlassenMap(state.klassenMap).merge(action.klasse);
		
		if (state.klasseUIModel) {
			return {...state, klassenMap: neueMap, klasseUIModel: {...state.klasseUIModel, uuid: klasse.uuid, name: klasse.name, saved: true}};
		}  else {
			return { ...state, klassenMap: neueMap, loading: false };
		}
	}),

	on(KlassenActions.klasseDeleted, (state, action) => {

		const neueMap = new KlassenMap(state.klassenMap).remove(action.klasse.uuid);
		return { ...state, klassenMap: neueMap, loading: false };
	}),

	on(KlassenActions.kindMoved, (state, action) => {

		const alteKlassenmap = new KlassenMap([...state.klassenMap]);
		const sourceKlasse = alteKlassenmap.get(action.sourceKlasseUuid);
		

		if (sourceKlasse) {
			const anzahlKinderSourceKlasse = sourceKlasse.anzahlKinder - 1;
			let anzahlLoesungszettelSourceKlasse = sourceKlasse.anzahlLoesungszettel;
			
			if (action.kind && action.kind.punkte) {
				anzahlLoesungszettelSourceKlasse = anzahlLoesungszettelSourceKlasse - 1;
			}

			const sourceKlasseToMerge = { ...sourceKlasse, anzahlKinder: anzahlKinderSourceKlasse, anzahlLoesungszettel: anzahlLoesungszettelSourceKlasse };
			const ersterMerge = alteKlassenmap.merge(sourceKlasseToMerge);
			const targetKlasse = new KlassenMap([...ersterMerge]).get(action.targetKlasseUuid);

			if (targetKlasse) {

				let anzahlLoesungszettelTargetKlasse = targetKlasse.anzahlLoesungszettel;

				if (action.kind && action.kind.punkte) {
					anzahlLoesungszettelTargetKlasse = anzahlLoesungszettelTargetKlasse + 1;
				}

				const anzahlKinderTargetKlasse = targetKlasse.anzahlKinder + 1;
				const targetKlasseToMerge = { ...targetKlasse, anzahlKinder: anzahlKinderTargetKlasse, anzahlLoesungszettel: anzahlLoesungszettelTargetKlasse };


				const neueMap = new KlassenMap(ersterMerge).merge(targetKlasseToMerge);
				return { ...state, klassenMap: neueMap };

			}		
		}
		return {...state};		
	}), 

	on(KlassenActions.dateiAusgewaehlt, (state, _action) => {
		return {...state, fehlermeldungenUploadReport: []};
	}),

	on(KlassenActions.navigatedToUploads, (state, _action) => {

		return {...state, importReport: undefined};
	}),

	on(KlassenActions.klassenlisteImportiert, (state, action) => {

		const klassen: Klasse[] = action.report.klassen;

		if (klassen.length === 0) {
			return state;
		}

		let klasse: Klasse = klassen[0];
		let neueMap = new KlassenMap(state.klassenMap).merge(klasse);

		for (let index = 1; index < klassen.length; index++) {
			const klasse: Klasse = klassen[index];
			neueMap = new KlassenMap(neueMap).merge(klasse);
		}

		return {...state, loading: false, klassenMap: neueMap, importReport: action.report};
	}),

	on(KlassenActions.markKlasseKorrigiert, (state, action) => {

		const klasseID = action.klasseID;

		if (state.selectedKlasse) {
			const selectedKlasse = state.selectedKlasse;
			if (klasseID === state.selectedKlasse.uuid) {
				const neueKlasse: Klasse = {...selectedKlasse, anzahlKinderZuPruefen: 0};
				const neueKlassenmap: KlasseWithID[] = new KlassenMap(state.klassenMap).merge(neueKlasse);
				return {...state, klassenMap: neueKlassenmap, selectedKlasse: neueKlasse};
			}
		}

		const klassenMap = new KlassenMap(state.klassenMap);

		if (klassenMap.has(klasseID)) {
			let geaenderteKlasse = {...klassenMap.get(klasseID), anzahlKinderZuPruefen: 0} as Klasse;
			const neueKlassenmap: KlasseWithID[] = klassenMap.merge(geaenderteKlasse);
			return {...state, klassenMap: neueKlassenmap};
		}

		return {...state};
	}),

	on(KlassenActions.alleKlassenGeloescht, (_state, _action) => {

		return initialKlassenState;
	})

);


export function reducer(state: KlassenState | undefined, action: Action) {
	return klassenReducer(state, action);
};

