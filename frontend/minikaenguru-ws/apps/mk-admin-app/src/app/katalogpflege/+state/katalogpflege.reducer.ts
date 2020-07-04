import { Action, createReducer, on } from '@ngrx/store';
import { Message } from '@minikaenguru-ws/common-messages';
import { KatalogpflegeItem, Katalogpflegetyp, mergeKatalogItems, Kataloge, mergeKatalogItemMap, KatalogpflegeItemWithID } from '../katalogpflege.model';
import * as KatalogpflegeActions from './katalogpflege.actions';

export const katalogpflegeFeatureKey = 'mk-admin-app-kataloge';


export interface KatalogpflegeState {
	readonly kataloge: Kataloge;
	readonly selectedKatalogItem: KatalogpflegeItem;
	readonly selectedKatalogTyp: Katalogpflegetyp;
	readonly showLoadingIndicator: boolean;
	readonly sucheBeendet: boolean
}

const initialState: KatalogpflegeState = {
	kataloge: { laender: [], orte: [], schulen: [] },
	selectedKatalogItem: undefined,
	selectedKatalogTyp: undefined,
	showLoadingIndicator: false,
	sucheBeendet: false
};

const katalogpflegeReducer = createReducer(initialState,

	on(KatalogpflegeActions.resetKataloge, (_state, _action) => {
		return initialState;
	}),

	on(KatalogpflegeActions.selectKatalogTyp, (state, action) => {

		return { ...state, selectedKatalogTyp: action.typ };

	}),

	on(KatalogpflegeActions.startSuche, (state, _action) => {

		return { ...state, showLoadingIndicator: true };
	}),

	on(KatalogpflegeActions.sucheFinished, (state, action) => {

		const alle: KatalogpflegeItem[] = action.katalogItems;
		const kataloge: Kataloge = mergeKatalogItems(alle, state.kataloge);
		return { ...state, kataloge: kataloge, showLoadingIndicator: false };
	}),

	on(KatalogpflegeActions.loadLaenderFinished, (state, action) => {

		const alle: KatalogpflegeItem[] = action.laender;
		let laenderWithID = [...state.kataloge.laender];

		alle.forEach(
			item => {
				laenderWithID = mergeKatalogItemMap(laenderWithID, item);
			}
		)

		const kataloge = {
			laender: laenderWithID,
			orte: [...state.kataloge.orte],
			schulen: [...state.kataloge.schulen]
		};

		return { ...state, kataloge: kataloge, showLoadingIndicator: false };
	}),

	on(KatalogpflegeActions.loadChildItemsFinished, (state, action) => {

		const parent: KatalogpflegeItem = {...action.parent, kinderGeladen: true};
		let laenderWithID = [...state.kataloge.laender];
		let orteWithID = [...state.kataloge.orte];
		let schulenWithID = [...state.kataloge.schulen];

		switch (parent.typ) {
			case 'LAND': laenderWithID = mergeKatalogItemMap(laenderWithID, parent); break;
			case 'ORT': orteWithID = mergeKatalogItemMap(orteWithID, parent); break;
		}

		let kataloge: Kataloge = {
			laender: laenderWithID,
			orte: orteWithID,
			schulen: schulenWithID
		};

		const children: KatalogpflegeItem[] = [];


		action.katalogItems.forEach(
			item => {
				children.push({...item, parent: parent});
			}
		);

		kataloge = mergeKatalogItems(children, kataloge);

		return { ...state, kataloge: kataloge, showLoadingIndicator: false };
	}),



	on(KatalogpflegeActions.sucheFinishedWithError, (state, _action) => {
		return { ...state, showLoadingIndicator: false };
	}),

	on(KatalogpflegeActions.selectKatalogItem, (state, action) => {
		return { ...state, selectedKatalogItem: action.katalogItem };
	}),

	on(KatalogpflegeActions.resetSelection, (state, _action) => {
		return { ...state, selectedKatalogItem: undefined, selectedKatalogTyp: undefined };
	})
);

export function reducer(state: KatalogpflegeState | undefined, action: Action) {
	return katalogpflegeReducer(state, action);
};

