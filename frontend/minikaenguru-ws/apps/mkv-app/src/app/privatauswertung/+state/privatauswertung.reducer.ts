import { createReducer, Action, on } from '@ngrx/store';
import * as PrivatauswertungActions from './privatauswertung.actions';
import { KindWithID, KinderMap } from '../privatauswertung.model';
import { initialKindEditorModel, KindEditorModel, Duplikatwarnung } from 'libs/common-components/src/lib/common-components.model';
import { Message } from '@minikaenguru-ws/common-messages';

export const privatauswertungFeatureKey = 'mkv-app-privatauswertung';

export interface PrivatauswertungState {
	kinderMap: KindWithID[];
	selectedKindUUID: string;
	kinderLoaded: boolean;
	loading: boolean;
	saveOutcome: Message;
	duplikatwarnung: Duplikatwarnung;
	editorModel: KindEditorModel;
};

const initialPrivatauswertungState: PrivatauswertungState = {
	kinderMap: [],
	selectedKindUUID: undefined,
	kinderLoaded: false,
	loading: false,
	saveOutcome: undefined,
	duplikatwarnung: undefined,
	editorModel: undefined
};

const privatauswertungReducer = createReducer(initialPrivatauswertungState,

	on(PrivatauswertungActions.startLoading, (state, _action) => {

		return { ...state, loading: true }
	}),

	on(PrivatauswertungActions.allKinderLoaded, (state, action) => {

		const alle = action.kinder;
		const newMap = [];
		alle.forEach(k => newMap.push({ uuid: k.uuid, kind: k }));

		return { ...state, loading: false, kinderLoaded: true, kinderMap: newMap };

	}),

	on(PrivatauswertungActions.startEditingKind, (state, action) => {

		const kind = action.kind;
		const kindEditorModel: KindEditorModel = {
			vorname: kind.vorname,
			nachname: kind.nachname ? kind.nachname : '',
			zusatz: kind.zusatz ? kind.zusatz : '',
			klassenstufe: kind.klassenstufe,
			sprache: kind.sprache

		};


		return { ...state, editorModel: kindEditorModel, saveOutcome: undefined };
	}),

	on(PrivatauswertungActions.createNewKind, (state, _action) => {
		return { ...state, editorModel: initialKindEditorModel }
	}),

	on(PrivatauswertungActions.finishedWithError, (state, _action) => {

		return { ...state, loading: false, kinderLoaded: false };

	}),

	on(PrivatauswertungActions.kindSaved, (state, action) => {
		const outcome = action.outcome;
		const neueMap = new KinderMap(state.kinderMap).merge(action.kind);
		return { ...state, kinderMap: neueMap, saveOutcome: outcome, loading: false };
	}),

	on(PrivatauswertungActions.duplikatGeprueft, (state, action) => {

		return { ...state, loading: false, duplikatwarnung: action.duplikatwarnung };

	}),

	on(PrivatauswertungActions.kindDeleted, (state, action) => {
		const outcome = action.outcome;
		const neueMap = new KinderMap(state.kinderMap).remove(action.kind.uuid)
		return { ...state, kinderMap: neueMap, saveOutcome: outcome, loading: false };
	}),


);

export function reducer(state: PrivatauswertungState | undefined, action: Action) {
	return privatauswertungReducer(state, action);
};

