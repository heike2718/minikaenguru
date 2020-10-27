import { createReducer, Action, on } from '@ngrx/store';
import * as PrivatauswertungActions from './privatauswertung.actions';
import { KindWithID } from '../privatauswertung.model';
import { initialKindEditorModel, KindEditorModel } from 'libs/common-components/src/lib/common-components.model';

export const privatauswertungFeatureKey = 'mkv-app-privatauswertung';

export interface PrivatauswertungState {
	kinderMap: KindWithID[];
	selectedKindUUID: string;
	kinderLoaded: boolean;
	loading: boolean;
	editorModel: KindEditorModel;
};

const initialPrivatauswertungState: PrivatauswertungState = {
	kinderMap: [],
	selectedKindUUID: undefined,
	kinderLoaded: false,
	loading: false,
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


		return {...state, editorModel: kindEditorModel};
	}),

	on(PrivatauswertungActions.createNewKind, (state, _action) => {
		return {...state, editorModel: initialKindEditorModel}
	}),

	on(PrivatauswertungActions.finishedWithError, (state, _action) => {

		return {...state, loading: false, kinderLoaded: false};

	})
);

export function reducer(state: PrivatauswertungState | undefined, action: Action) {
	return privatauswertungReducer(state, action);
};

