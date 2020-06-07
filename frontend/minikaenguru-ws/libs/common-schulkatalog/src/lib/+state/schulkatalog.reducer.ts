import { Action, createReducer, on, State } from '@ngrx/store';
import { KatalogItem, Katalogtyp } from '../domain/entities';
import * as SchulkatalogActions from './schulkatalog.actions';

export const schulkatalogFeatureKey = 'schulkatalog';

export interface KatalogSucheTexte {
	readonly inputLabel: string;
	readonly sucheDescription: string;
	readonly auswahlDescription: string;
}

const initialKatalogsucheTexte: KatalogSucheTexte = {
	inputLabel: '',
	sucheDescription: '',
	auswahlDescription: '',
}

export interface GuiModel {
	readonly texte: KatalogSucheTexte;
	readonly showInputControl: boolean;
	readonly showLoadingIndicator: boolean;
	readonly katalogItemsAvailable: boolean;
}

const initialGuiModel: GuiModel = {
	texte: initialKatalogsucheTexte,
	showInputControl: false, // 8
	showLoadingIndicator: false, // 9
	katalogItemsAvailable: false // 10
};

export interface SchulkatalogState {
	readonly currentKatalogtyp: Katalogtyp, // 1
	readonly guiModel: GuiModel,
	readonly loadedKatalogItems: KatalogItem[],
	readonly searchTerm: string,
	readonly selectedKatalogItem: KatalogItem
};

const initialState: SchulkatalogState = {
	currentKatalogtyp: undefined, // 1
	guiModel: initialGuiModel,
	loadedKatalogItems: [],
	searchTerm: '',
	selectedKatalogItem: undefined
};

const schulkatalogReducer = createReducer(
	initialState,

	on(SchulkatalogActions.initSchulkatalog, (state, action) => {

		const katalogtyp = action.katalogtyp;

		const inputLabel = getInputLabel(katalogtyp, undefined);
		const sucheDescription = getSucheDescription(katalogtyp, undefined);

		const texte: KatalogSucheTexte = {
			...state.guiModel.texte,
			inputLabel: inputLabel,
			sucheDescription: sucheDescription
		}

		const guiModel = {
			...initialGuiModel
			, texte: texte
			, showInputControl: true
			, showLoadingIndicator: false
		};

		return { ...initialState, guiModel: guiModel, currentKatalogtyp: katalogtyp };
	}),

	on(SchulkatalogActions.startSearch, (state, action) => {
		const guiModel = { ...initialGuiModel, showLoadingIndicator: true };
		return { ...state, searchTerm: action.searchTerm, guiModel: guiModel }
	}),

	on(SchulkatalogActions.searchFinished, (state, action) => {

		const loadedKatalogItems = action.katalogItems;
		const auswahlDescription = getAuswahlDescriptiom(loadedKatalogItems);
		const katalogItemsAvailable = loadedKatalogItems.length > 0;

		let texte: KatalogSucheTexte = {
			...state.guiModel.texte,
			auswahlDescription: auswahlDescription
		};

		let guiModel = {
			...state.guiModel
			, texte: texte
			, showLoadingIndicator: false
			, katalogItemsAvailable: katalogItemsAvailable
		};

		let typ = state.currentKatalogtyp;

		if (loadedKatalogItems.length > action.immediatelyLoadOnNumberChilds) {

			typ = loadedKatalogItems[0].typ;

			texte = {
				...texte,
				inputLabel: getInputLabel(typ, undefined),
				sucheDescription: getSucheDescription(typ, undefined)
			};

			guiModel = {
				...guiModel
				, showInputControl: true
				, texte: texte
			}
		}

		return {
			...state
			, currentKatalogtyp: typ
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

		const texte = {
			...state.guiModel.texte,
			auswahlDescription: auswahlDescription
		};

		const guiModel = {
			...state.guiModel,
			texte: texte,
			showLoadingIndicator: false,
			katalogItemsAvailable: katalogItemsAvailable
		};

		return {
			...state,
			guiModel: guiModel,
			loadedKatalogItems: loadedKatalogItems,
			searchTerm: '',
			selectedKatalogItem: undefined
		}
	}),

	on(SchulkatalogActions.searchError, (state, _action) => {

		const katalogtyp = state.currentKatalogtyp;

		const texte: KatalogSucheTexte = {
			...state.guiModel.texte,
			inputLabel: getInputLabel(katalogtyp, undefined),
			sucheDescription: getSucheDescription(katalogtyp, undefined)
		}

		const guiModel = {
			...state.guiModel
			, texte: texte
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
		const katalogtyp = state.currentKatalogtyp

		if (!selectedKatalogItem) {
			const inputLabel = getInputLabel(katalogtyp, undefined);
			const sucheDescription = getSucheDescription(katalogtyp, undefined);

			const texte: KatalogSucheTexte = {
				...state.guiModel.texte,
				inputLabel: inputLabel,
				sucheDescription: sucheDescription,
				auswahlDescription: ''
			}

			const guiModel = {
				...initialGuiModel,
				texte: texte,
				showLoadingIndicator: false
			}

			return { ...initialState, guiModel: guiModel };
		}

		const neuerKatalogtyp = getCurrentKatalogtyp(katalogtyp, selectedKatalogItem);

		const texte: KatalogSucheTexte = {
			...state.guiModel.texte,
			inputLabel: getInputLabel(neuerKatalogtyp, selectedKatalogItem),
			sucheDescription: getSucheDescription(neuerKatalogtyp, selectedKatalogItem),
			auswahlDescription: ''

		}

		let guiModel = {
			...state.guiModel,
			texte: texte,
			showLoadingIndicator: false
		};

		if (selectedKatalogItem.anzahlKinder <= action.immediatelyLoadOnNumberChilds) {
			guiModel = { ...guiModel, showInputControl: false }
		} else {
			guiModel = { ...guiModel, showInputControl: true }
		}

		return { ...state, guiModel: guiModel, loadedKatalogItems: [], searchTerm: '', currentKatalogtyp: neuerKatalogtyp, selectedKatalogItem: selectedKatalogItem };
	})
);

export function reducer(state: SchulkatalogState | undefined, action: Action) {
	return schulkatalogReducer(state, action);
}

// private functions
function getSucheDescription(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): string {

	let result = '';

	if (selectedKatalogItem) {
		switch (selectedKatalogItem.typ) {
			case 'LAND':
				result = 'Bitte suchen Sie Ihren Ort (mindestens 3 Buchstaben).';
				break;
			case 'ORT':
				result = 'Bitte suchen Sie Ihre Schule (mindestens 3 Buchstaben).';
				break
			case 'SCHULE':
				return '';
		}
		return result;
	}

	switch (alterKatalogtyp) {
		case 'LAND':
			result = 'Bitte geben Sie die Anfangsbuchstaben Ihres Landes ein (mindestens 3 Buchstaben).';
			break;
		case 'ORT':
			result = 'Bitte geben Sie die Anfangsbuchstaben Ihres Ortes ein (mindestens 3 Buchstaben).';
			break;
		case 'SCHULE':
			result = 'Bitte geben Sie die Anfangsbuchstaben Ihrer Schule ein';
			break;
	}

	return result;
}

function getInputLabel(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): string {

	if (selectedKatalogItem) {
		switch (selectedKatalogItem.typ) {
			case 'LAND':
				return 'Ort';
			case 'ORT':
				return 'Schule';
			case 'SCHULE':
				return '';
		}
	}

	switch (alterKatalogtyp) {
		case 'LAND':
			return 'Land';
		case 'ORT':
			return 'Ort';
		case 'SCHULE':
			return 'Schule';
	}

	return '';
}

function getAuswahlDescriptiom(katalogItems: KatalogItem[]): string {

	if (katalogItems.length === 0) {
		return '0 Treffer';
	}

	const katalogtyp = katalogItems[0].typ;
	let result = katalogItems.length + ' Treffer. ';

	switch (katalogtyp) {
		case 'LAND':
			result += 'Bitte wählen Sie Ihr Land aus.';
			break;
		case 'ORT':
			result += 'Bitte wählen Sie Ihren Ort aus.';
			break;
		case 'SCHULE':
			result += 'Bitte wählen Sie Ihre Schule aus.';
			break;
	}

	return result;
}

function getCurrentKatalogtyp(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): Katalogtyp {

	if (selectedKatalogItem === undefined) {
		return alterKatalogtyp;
	}
	return selectedKatalogItem.typ;
}
