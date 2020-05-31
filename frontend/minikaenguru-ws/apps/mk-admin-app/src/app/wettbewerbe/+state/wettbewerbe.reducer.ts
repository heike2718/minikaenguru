import { Action, createReducer, on } from '@ngrx/store';
import * as WettbewerbeActions from './wettbewerbe.actions';
import { WettbewerbWithID, WettbewerbEditorModel, initialWettbewerbEditorModel, Wettbewerb } from '../wettbewerbe.model';

import { Message } from '@minikaenguru-ws/common-messages';

export const wettbewerbeFeatureKey = 'mk-admin-app-wettbewerbe';

export interface WettbewerbeState {
	wettbewerbeMap: WettbewerbWithID[],
	selectedJahr: number,
	wettbewerbeLoaded: boolean;
	saveOutcome: Message;
	wettbewerbEditorModel: WettbewerbEditorModel;
};

export const initialWettbewerbeState = {
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

		const alteMap = state.wettbewerbeMap;
		let neueMap: WettbewerbWithID[] = [];

		const selectedJahr = state.selectedJahr;
		for (let i: number = 0; i < alteMap.length; i++) {
			const wbMitId: WettbewerbWithID = alteMap[i];
			if (wbMitId.jahr !== selectedJahr) {
				neueMap.push(wbMitId);
			} else {
				neueMap.push({ jahr: action.wettbewerb.jahr, wettbewerb: action.wettbewerb });
			}
		}

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
			datumFreischaltungPrivat: wettbewerb.datumFreischaltungPrivat
		};

		return { ...state, wettbewerbEditorModel: wettbewerbEditorModel, saveOutcome: undefined };
	}),

	on(WettbewerbeActions.editWettbewerbFinished, (state, _action) => {

		return { ...state, selectedJahr: undefined, saveOutcome: undefined, wettbewerbEditorModel: undefined }

	}),

	on(WettbewerbeActions.wettbewerbSaved, (state, action) => {

		const outcome = action.outcome;

		if (outcome.level === 'INFO') {

			const alteMap = state.wettbewerbeMap;
			let neueMap: WettbewerbWithID[] = [];

			const selectedJahr = state.selectedJahr;
			for (let i: number = 0; i < alteMap.length; i++) {
				const wbMitId: WettbewerbWithID = alteMap[i];
				if (wbMitId.jahr !== selectedJahr) {
					neueMap.push(wbMitId);
				}
			}
			neueMap.push({ jahr: action.wettbewerb.jahr, wettbewerb: action.wettbewerb });

			// const neueMap: WettbewerbWithID[] = mergeWettbewerb(state.wettbewerbeMap, action.wettbewerb);
			console.debug('updateWettbewerb: status fertig')
			return { ...state, wettbewerbeMap: neueMap, selectedJahr: action.wettbewerb.jahr, saveOutcome: outcome };
		} else {
			return { ...state, saveOutcome: outcome };
		}
	})

);

export function reducer(state: WettbewerbeState | undefined, action: Action) {
	return wettbewerbeReducer(state, action);
}
