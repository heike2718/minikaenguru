import { createReducer, Action, on } from '@ngrx/store';
import * as KinderActions from './kinder.actions';
import { KindWithID, KinderMap, KindEditorVorbelegung, KlassenwechselDaten } from '../kinder.model';
import { initialKindEditorModel, KindEditorModel, Duplikatwarnung, Teilnahmeart, TeilnahmeIdentifierAktuellerWettbewerb, Klassenstufe, Kind } from '@minikaenguru-ws/common-components';
import { Message } from '@minikaenguru-ws/common-messages';

export const kinderFeatureKey = 'mkv-app-kinder';

export interface KinderState {
	readonly teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb;
	readonly kinderMap: KindWithID[];
	readonly selectedKind: Kind,
	readonly kinderLoaded: boolean;
	readonly loading: boolean;
	readonly saveOutcome: Message;
	readonly duplikatwarnung: Duplikatwarnung;
	readonly editorModel: KindEditorModel;
	readonly editorVorbelegung: KindEditorVorbelegung;

};

const initialKinderState: KinderState = {
	teilnahmeIdentifier: undefined,
	kinderMap: [],
	selectedKind: undefined,
	kinderLoaded: false,
	loading: false,
	saveOutcome: undefined,
	duplikatwarnung: undefined,
	editorModel: initialKindEditorModel,
	editorVorbelegung: { klassenstufe: null, sprache: {sprache: 'de', label: 'deutsch'} }
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

	on(KinderActions.selectKind, (state, action) => {
		return {...state, selectedKind: action.kind};
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


		return { ...state, selectedKind: kind, editorModel: kindEditorModel, saveOutcome: undefined };
	}),

	on(KinderActions.createNewKind, (state, action) => {

		const kind: Kind = {
			uuid: 'neu',
			klasseId: action.klasseUuid,
			klassenstufe: state.editorVorbelegung.klassenstufe,
			sprache: state.editorVorbelegung.sprache,
			vorname: '',
			loesungszettelId: undefined,
			nachname: undefined,
			zusatz: undefined
		};

		return {
			...state,
			selectedKind: kind,
			editorModel: { ...initialKindEditorModel, klasseId: action.klasseUuid, klassenstufe: state.editorVorbelegung.klassenstufe, sprache: state.editorVorbelegung.sprache },
			duplikatwarnung: undefined
		};
	}),

	on(KinderActions.finishedWithError, (state, _action) => {

		return { ...state, loading: false, kinderLoaded: false, duplikatwarnung: undefined };

	}),

	on(KinderActions.kindSaved, (state, action) => {
		const outcome = action.outcome;
		const neueMap = new KinderMap(state.kinderMap).merge(action.kind);

		const editorVorbelegung: KindEditorVorbelegung = {
			klassenstufe: action.kind.klassenstufe,
			sprache: action.kind.sprache
		};

		return { ...state,
			kinderMap: neueMap,
			saveOutcome: outcome,
			loading: false,
			duplikatwarnung: undefined,
			editorVorbelegung: editorVorbelegung,
			selectedKind: action.kind
		 };
	}),

	on(KinderActions.duplikatGeprueft, (state, action) => {

		return { ...state, loading: false, duplikatwarnung: action.duplikatwarnung };

	}),

	on(KinderActions.editCancelled, (state, _action) => {
		return { ...state, selectedKind: undefined, editorModel: undefined, saveOutcome: undefined, loading: false, duplikatwarnung: undefined};
	}),

	on(KinderActions.kindDeleted, (state, action) => {
		const outcome = action.outcome;
		const neueMap = new KinderMap(state.kinderMap).remove(action.kind.uuid)
		return { ...state, kinderMap: neueMap, saveOutcome: outcome, loading: false, duplikatwarnung: undefined };
	}),

	on(KinderActions.resetModule, (_state, _action) => {
		return initialKinderState;
	}),


);

export function reducer(state: KinderState | undefined, action: Action) {
	return kinderReducer(state, action);
};

