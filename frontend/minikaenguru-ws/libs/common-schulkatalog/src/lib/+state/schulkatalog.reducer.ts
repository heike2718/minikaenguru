import { Action, createReducer, on, State } from '@ngrx/store';
import { KatalogItem, GuiModel, getInputLabel, getSucheDescription, getAuswahlDescriptiom, getCurrentKatalogtyp } from '../domain/entities';
import * as SchulkatalogActions from './schulkatalog.actions';

export const schulkatalogFeatureKey = 'schulkatalog';

export interface SchulkatalogState {
	guiModel: GuiModel,
	loadedKatalogItems: KatalogItem[],
	searchTerm: string,
	selectedKatalogItem: KatalogItem
};

export const initialGuiModel: GuiModel = {
	currentKatalogtyp: undefined, // 1
	sucheDescription: '', // 5
	inputLabel: '', // 6
	auswahlDescription: '', // 7
	showInputControl: false, // 8
	showLoadingIndicator: false, // 9
	katalogItemsAvailable: false // 10
};

export const initialState: SchulkatalogState = {
	guiModel: initialGuiModel,
	loadedKatalogItems: [],
	searchTerm: '',
	selectedKatalogItem: undefined
};

const schulkatalogReducer = createReducer(
	initialState,

	on(SchulkatalogActions.initSchulkatalog, (_state, action) => {

		const katalogtyp = action.katalogtyp;

		const inputLabel = getInputLabel(katalogtyp, undefined);
		const sucheDescription = getSucheDescription(katalogtyp, undefined);

		const guiModel = {
			...initialGuiModel
			, currentKatalogtyp: katalogtyp
			, inputLabel: inputLabel
			, sucheDescription: sucheDescription
			, showInputControl: true
			, showLoadingIndicator: false
		};

		return { ...initialState, guiModel: guiModel };
	}),

	on(SchulkatalogActions.startSearch, (state, action) => {
		const guiModel = { ...initialGuiModel, showLoadingIndicator: true };
		return { ...state, searchTerm: action.searchTerm, guiModel: guiModel }
	}),

	on(SchulkatalogActions.searchFinished, (state, action) => {

		const loadedKatalogItems = action.katalogItems;
		const auswahlDescription = getAuswahlDescriptiom(loadedKatalogItems);
		const katalogItemsAvailable = loadedKatalogItems.length > 0;

		let guiModel = {
			...state.guiModel
			, showLoadingIndicator: false
			, auswahlDescription: auswahlDescription
			, katalogItemsAvailable: katalogItemsAvailable
		};

		if (loadedKatalogItems.length > action.immediatelyLoadOnNumberChilds) {

			const neuerTyp = loadedKatalogItems[0].typ;
			guiModel = {
				...guiModel
				, showInputControl: true
				, currentKatalogtyp: neuerTyp
				, inputLabel: getInputLabel(neuerTyp, undefined)
				, sucheDescription: getSucheDescription(neuerTyp, undefined)
			}
		}

		return {
			...state
			, guiModel: guiModel
			, loadedKatalogItems: loadedKatalogItems
			, searchTerm: ''
			, selectedKatalogItem: undefined
		}
	}),

	on(SchulkatalogActions.startLoadChildItems, (state, _action) => {
		const guiModel = { ...state.guiModel, showLoadingIndicator: true };
		return { ...state, guiModel: guiModel }
	}),

	on(SchulkatalogActions.childItemsLoaded, (state, action) => {

		const loadedKatalogItems = action.katalogItems;
		const auswahlDescription = getAuswahlDescriptiom(loadedKatalogItems);
		const katalogItemsAvailable = loadedKatalogItems.length > 0;

		const guiModel = {
			...state.guiModel
			, showLoadingIndicator: false
			, auswahlDescription: auswahlDescription
			, katalogItemsAvailable: katalogItemsAvailable
		};

		return {
			...state
			, guiModel: guiModel
			, loadedKatalogItems: loadedKatalogItems
			, searchTerm: ''
			, selectedKatalogItem: undefined
		}
	}),

	on(SchulkatalogActions.searchError, (state, _action) => {

		const katalogtyp = state.guiModel.currentKatalogtyp;

		const guiModel = {
			...state.guiModel
			, inputLabel: getInputLabel(katalogtyp, undefined)
			, sucheDescription: getSucheDescription(katalogtyp, undefined)
			, showInputControl: true
			, showLoadingIndicator: false
			, katalogItemsAvailable: false
		};

		return {
			...state
			, guiModel: guiModel
			, loadedKatalogItems: []
			, searchTerm: ''
			, selectedKatalogItem: undefined
		}
	}),

	on(SchulkatalogActions.katalogItemSelected, (state, action) => {

		const selectedKatalogItem = action.katalogItem;
		const katalogtyp = state.guiModel.currentKatalogtyp

		if (!selectedKatalogItem) {
			const inputLabel = getInputLabel(katalogtyp, undefined);
			const sucheDescription = getSucheDescription(katalogtyp, undefined);

			const guiModel = {
				...initialGuiModel
				, currentKatalogtyp: katalogtyp
				, inputLabel: inputLabel
				, sucheDescription: sucheDescription
				, auswahlDescription: ''
				, showLoadingIndicator: false
			}
			return { ...initialState, guiModel: guiModel };
		}

		const neuerKatalogtyp = getCurrentKatalogtyp(katalogtyp, selectedKatalogItem);

		let guiModel = {
			...state.guiModel
			, currentKatalogtyp: neuerKatalogtyp
			, inputLabel: getInputLabel(neuerKatalogtyp, selectedKatalogItem)
			, sucheDescription: getSucheDescription(neuerKatalogtyp, selectedKatalogItem)
			, auswahlDescription: ''
			, showLoadingIndicator: false
		};

		if (selectedKatalogItem.anzahlKinder <= action.immediatelyLoadOnNumberChilds) {
			guiModel = { ...guiModel, showInputControl: false }
		} else {
			guiModel = { ...guiModel, showInputControl: true }
		}

		return { ...state, guiModel: guiModel, loadedKatalogItems: [], searchTerm: '', selectedKatalogItem: selectedKatalogItem };
	})
);

export function reducer(state: SchulkatalogState | undefined, action: Action) {
	return schulkatalogReducer(state, action);
}
