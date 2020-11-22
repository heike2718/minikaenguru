import { createReducer, Action, on } from '@ngrx/store';
import * as KinderActions from './kinder.actions';
import { KindWithID, KinderMap } from '../kinder.model';
import { initialKindEditorModel, KindEditorModel, Duplikatwarnung, Teilnahmeart, TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';

export const kinderFeatureKey = 'mkv-app-kinder';

export interface KinderState {
	teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb;
	kinderMap: KindWithID[];
	selectedKindUUID: string;
	kinderLoaded: boolean;
	loading: boolean;
	saveOutcome: Message;
	duplikatwarnung: Duplikatwarnung;
	editorModel: KindEditorModel;
};

const initialKinderState: KinderState = {
	teilnahmeIdentifier: undefined,
	kinderMap: [],
	selectedKindUUID: undefined,
	kinderLoaded: false,
	loading: false,
	saveOutcome: undefined,
	duplikatwarnung: undefined,
	editorModel: initialKindEditorModel
};

const kinderReducer = createReducer(initialKinderState,

	on(KinderActions.teilnahmenummerInitialized, (state, action) => {

		return { ...state, teilnahmeIdentifier: action.teilnahmeIdentifier };
	}),


	on(KinderActions.startLoading, (state, _action) => {

		return { ...state, loading: true }
	}),

	on(KinderActions.allKinderLoaded, (state, action) => {

		const alle = action.kinder;
		const newMap = [];
		alle.forEach(k => newMap.push({ uuid: k.uuid, kind: k }));

		return { ...state, loading: false, kinderLoaded: true, kinderMap: newMap };

	}),

	on(KinderActions.startEditingKind, (state, action) => {

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

	on(KinderActions.createNewKind, (state, action) => {

		return { ...state, editorModel: {...initialKindEditorModel, klasseId: action.klasseUuid} };
	}),

	on(KinderActions.finishedWithError, (state, _action) => {

		return { ...state, loading: false, kinderLoaded: false };

	}),

	on(KinderActions.kindSaved, (state, action) => {
		const outcome = action.outcome;
		const neueMap = new KinderMap(state.kinderMap).merge(action.kind);
		return { ...state, kinderMap: neueMap, saveOutcome: outcome, loading: false };
	}),

	on(KinderActions.duplikatGeprueft, (state, action) => {

		return { ...state, loading: false, duplikatwarnung: action.duplikatwarnung };

	}),

	on(KinderActions.editCancelled, (state, _action) => {
		return { ...state, editorModel: undefined, saveOutcome: undefined, loading: false };
	}),

	on(KinderActions.kindDeleted, (state, action) => {
		const outcome = action.outcome;
		const neueMap = new KinderMap(state.kinderMap).remove(action.kind.uuid)
		return { ...state, kinderMap: neueMap, saveOutcome: outcome, loading: false };
	}),

	on(KinderActions.resetModule, (_state, _action) => {
		return initialKinderState;
	}),


);

export function reducer(state: KinderState | undefined, action: Action) {
	return kinderReducer(state, action);
};

