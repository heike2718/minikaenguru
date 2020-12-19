import { Action, createReducer, on } from '@ngrx/store';
import * as WettbewerbeActions from './wettbewerbe.actions';
import { WettbewerbWithID, WettbewerbEditorModel, initialWettbewerbEditorModel, Wettbewerb, WettbewerbeMap, WettbewerbStatus } from '../wettbewerbe.model';

import { Message } from '@minikaenguru-ws/common-messages';

export const wettbewerbeFeatureKey = 'mk-admin-app-wettbewerbe';

export interface WettbewerbeState {
	readonly wettbewerbeMap: WettbewerbWithID[],
	readonly selectedJahr: number,
	readonly wettbewerbeLoaded: boolean;
	readonly saveOutcome: Message;
	readonly wettbewerbEditorModel: WettbewerbEditorModel;
};

const initialWettbewerbeState = {
	wettbewerbeMap: [],
	selectedJahr: undefined,
	wettbewerbeLoaded: false,
	saveOutcome: undefined,
	wettbewerbEditorModel: undefined
} as WettbewerbeState;

const wettbewerbeReducer = createReducer(initialWettbewerbeState,

	on(WettbewerbeActions.allWettbewerbeLoaded, (state, action) => {

		const alle = action.wettbewerbe;
		const newMap = [];
		alle.forEach(w => newMap.push({ jahr: w.jahr, wettbewerb: w }));
		return { ...state, wettbewerbeMap: newMap, selectedJahr: undefined, wettbewerbeLoaded: true, saveOutcome: undefined }
	}),

	on(WettbewerbeActions.selectWettbewerbsjahr, (state, action) => {
		const loaded = state.wettbewerbeMap.length > 0;
		return { ...state, selectedJahr: action.jahr, wettbewerbeLoaded: loaded, saveOutcome: undefined }
	}),

	on(WettbewerbeActions.selectedWettbewerbLoaded, (state, action) => {

		const neueMap = new WettbewerbeMap(state.wettbewerbeMap).merge(action.wettbewerb);
		const loaded = state.wettbewerbeMap.length > 0;
		return { ...state, wettbewerbeMap: neueMap, selectedJahr: action.wettbewerb.jahr, wettbewerbeLoaded: loaded, saveOutcome: undefined }
	}),

	on(WettbewerbeActions.resetWettbewerbe, (_state, _action) => {
		return initialWettbewerbeState
	}),

	on(WettbewerbeActions.createNewWettbewerb, (state, _action) => {

		const modelPart: WettbewerbEditorModel = initialWettbewerbEditorModel;
		return { ...state, selectedJahr: modelPart.jahr, saveOutcome: undefined, wettbewerbEditorModel: modelPart };
	}),

	on(WettbewerbeActions.startEditingWettbewerb, (state, action) => {

		const wettbewerb: Wettbewerb = action.wettbewerb;
		const wettbewerbEditorModel: WettbewerbEditorModel = {
			jahr: wettbewerb.jahr,
			status: wettbewerb.status,
			wettbewerbsbeginn: wettbewerb.wettbewerbsbeginn,
			wettbewerbsende: wettbewerb.wettbewerbsende,
			datumFreischaltungLehrer: wettbewerb.datumFreischaltungLehrer,
			datumFreischaltungPrivat: wettbewerb.datumFreischaltungPrivat,
			loesungsbuchstabenIkids: wettbewerb.loesungsbuchstabenIkids,
			loesungsbuchstabenKlasse1: wettbewerb.loesungsbuchstabenKlasse1,
			loesungsbuchstabenKlasse2: wettbewerb.loesungsbuchstabenKlasse2
		};

		return { ...state, wettbewerbEditorModel: wettbewerbEditorModel, saveOutcome: undefined };
	}),

	on(WettbewerbeActions.editWettbewerbFinished, (state, _action) => {

		return { ...state, selectedJahr: undefined, saveOutcome: undefined, wettbewerbEditorModel: undefined };

	}),

	on(WettbewerbeActions.wettbewerbInserted, (state, action) => {

		const outcome = action.outcome;
		const neueMap = [...state.wettbewerbeMap, { jahr: action.wettbewerb.jahr, wettbewerb: action.wettbewerb }];
		return { ...state, wettbewerbeMap: neueMap, selectedJahr: action.wettbewerb.jahr, saveOutcome: outcome };
	}),

	on(WettbewerbeActions.wettbewerbUpdated, (state, action) => {

		const outcome = action.outcome;
		const neueMap = new WettbewerbeMap(state.wettbewerbeMap).merge(action.wettbewerb);
		return { ...state, wettbewerbeMap: neueMap, selectedJahr: action.wettbewerb.jahr, saveOutcome: outcome };
	}),

	on(WettbewerbeActions.saveFailed, (state, action) => {
		return { ...state, saveOutcome: action.outcome };
	}),

	on(WettbewerbeActions.wettbewerbMovedOn, (state, action) => {
		const outcome = action.outcome;
		const neuerStatus: WettbewerbStatus = action.neuerStatus;
		const neuerWettbewerb: Wettbewerb = { ...action.wettbewerb, status: neuerStatus };
		const neueMap = new WettbewerbeMap(state.wettbewerbeMap).merge(neuerWettbewerb);

		return { ...state, wettbewerbeMap: neueMap, selectedJahr: action.wettbewerb.jahr, saveOutcome: outcome };
	})

);

export function reducer(state: WettbewerbeState | undefined, action: Action) {
	return wettbewerbeReducer(state, action);
};
