import { createReducer, Action, on } from '@ngrx/store';
import * as KlassenActions from './klassen.actions';
import { KlasseWithID, KlassenMap } from '../klassen.model';
import { KlasseEditorModel, initialKlasseEditorModel, Klasse } from '@minikaenguru-ws/common-components';
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

	on(KlassenActions.klasseSaved, (state, action) => {

		const neueMap = new KlassenMap(state.klassenMap).merge(action.klasse);
		return { ...state, klassenMap: neueMap, loading: false };
	}),

	on(KlassenActions.klasseDeleted, (state, action) => {

		const neueMap = new KlassenMap(state.klassenMap).remove(action.klasse.uuid);
		return { ...state, klassenMap: neueMap, loading: false };
	})

);


export function reducer(state: KlassenState | undefined, action: Action) {
	return klassenReducer(state, action);
};

