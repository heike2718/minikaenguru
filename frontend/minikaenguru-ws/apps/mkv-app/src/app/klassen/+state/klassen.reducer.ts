import { createReducer, Action, on } from '@ngrx/store';
import * as KlassenActions from './klassen.actions';
import { KlasseWithID, KlassenMap } from '../klassen.model';
import { KlasseEditorModel, initialKlasseEditorModel, Klasse, Kind } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';


export const klassenFeatureKey = 'mkv-app-klassen';

export interface KlassenState {
	klassenMap: KlasseWithID[];
	klassenLoaded: boolean;
	loading: boolean;
	editorModel: KlasseEditorModel;
	selectedKlasse: Klasse;
};

const initialKlassenState: KlassenState = {
	klassenMap: [],
	klassenLoaded: false,
	loading: false,
	editorModel: undefined,
	selectedKlasse: undefined
};

const klassenReducer = createReducer(initialKlassenState,

	on(KlassenActions.resetModule, (_state, _action) => {
		return initialKlassenState;
	}),

	on(KlassenActions.allKlassenLoaded, (state, action) => {

		const alle = action.klassen;
		const newMap = [];
		alle.forEach(k => newMap.push({ uuid: k.uuid, klasse: k }));


		return { ...state, klassenLoaded: true, klassenMap: newMap, loading: false };
	}),

	on(KlassenActions.createNewKlasse, (state, _action) => {

		return { ...state, editorModel: initialKlasseEditorModel };


	}),

	on(KlassenActions.startEditingKlasse, (state, action) => {

		const klasse = action.klasse;
		const klasseEditorModel: KlasseEditorModel = {
			name: klasse.name
		};

		return { ...state, editorModel: klasseEditorModel, selectedKlasse: klasse };

	}),

	on(KlassenActions.startAssigningKinder, (state, action) => {

		const klasse = action.klasse;
		return { ...state, editorModel: undefined, selectedKlasse: klasse };

	}),

	on(KlassenActions.kindAdded, (state, _action) => {

		const anzahlKinder = state.selectedKlasse.anzahlKinder + 1;
		const selectedKlasse = { ...state.selectedKlasse, anzahlKinder };
		const merged: KlasseWithID[] = new KlassenMap(state.klassenMap).merge(selectedKlasse);

		return { ...state, selectedKlasse: selectedKlasse, klassenMap: merged };

	}),

	on(KlassenActions.kindDeleted, (state, action) => {

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
		const klasseID = kind.klasseId;
		if (klasseID) {

			if (state.selectedKlasse && state.selectedKlasse.uuid === klasseID) {
				const anzahlLoesungszettel = state.selectedKlasse.anzahlLoesungszettel + 1;
				const neueGewaehlteKlasse = {...state.selectedKlasse, anzahlLoesungszettel: anzahlLoesungszettel};

				const neueMap = new KlassenMap(state.klassenMap).merge(neueGewaehlteKlasse);

				return {...state, selectedKlasse: neueGewaehlteKlasse, klassenMap: neueMap};
			}

		} else {

			return { ...state };
		}
	}),

	on(KlassenActions.loesungszettelDeleted, (state, action) => {

		const kind: Kind = action.kind;
		const klasseID = kind.klasseId;
		if (klasseID) {

			if (state.selectedKlasse && state.selectedKlasse.uuid === klasseID) {
				const anzahlLoesungszettel = state.selectedKlasse.anzahlLoesungszettel - 1;
				const neueGewaehlteKlasse = {...state.selectedKlasse, anzahlLoesungszettel: anzahlLoesungszettel};

				const neueMap = new KlassenMap(state.klassenMap).merge(neueGewaehlteKlasse);

				return {...state, selectedKlasse: neueGewaehlteKlasse, klassenMap: neueMap};
			}

		} else {

			return { ...state };
		}
	}),

	on(KlassenActions.klasseSaved, (state, action) => {

		const neueMap = new KlassenMap(state.klassenMap).merge(action.klasse);
		return { ...state, klassenMap: neueMap, loading: false };
	}),

	on(KlassenActions.klasseDeleted, (state, action) => {

		const neueMap = new KlassenMap(state.klassenMap).remove(action.klasse.uuid);
		return { ...state, klassenMap: neueMap, loading: false };
	}),

	on(KlassenActions.kindMoved, (state, action) => {

		const alteKlassenmap = new KlassenMap([...state.klassenMap]);

		const sourceKlasse = alteKlassenmap.get(action.sourceKlasseUuid);
		const anzahlKinderSourceKlasse = sourceKlasse.anzahlKinder - 1;

		let anzahlLoesungszettelSourceKlasse = sourceKlasse.anzahlLoesungszettel;

		if (action.kind && action.kind.punkte) {
			anzahlLoesungszettelSourceKlasse = anzahlLoesungszettelSourceKlasse - 1;
		}

		const sourceKlasseToMerge = { ...sourceKlasse, anzahlKinder: anzahlKinderSourceKlasse, anzahlLoesungszettel: anzahlLoesungszettelSourceKlasse };

		const ersterMerge = alteKlassenmap.merge(sourceKlasseToMerge);

		const targetKlasse = new KlassenMap([...ersterMerge]).get(action.targetKlasseUuid);
		let anzahlLoesungszettelTargetKlasse = targetKlasse.anzahlLoesungszettel;

		if (action.kind && action.kind.punkte) {
			anzahlLoesungszettelTargetKlasse = anzahlLoesungszettelTargetKlasse + 1;
		}

		const anzahlKinderTargetKlasse = targetKlasse.anzahlKinder + 1;
		const targetKlasseToMerge = { ...targetKlasse, anzahlKinder: anzahlKinderTargetKlasse, anzahlLoesungszettel: anzahlLoesungszettelTargetKlasse };


		const neueMap = new KlassenMap(ersterMerge).merge(targetKlasseToMerge);
		return { ...state, klassenMap: neueMap };
	})

);


export function reducer(state: KlassenState | undefined, action: Action) {
	return klassenReducer(state, action);
};

