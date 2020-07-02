import { Action, createReducer, on, State } from '@ngrx/store';
import { KatalogItem, Katalogtyp } from '../domain/entities';
import * as SchulkatalogActions from './schulkatalog.actions';

export const schulkatalogFeatureKey = 'schulkatalog';

export interface KatalogSucheTexte {
	readonly subtitle: string;
	readonly inputLabel: string;
	readonly sucheDescription: string;
	readonly auswahlDescription: string
}

const initialKatalogsucheTexte: KatalogSucheTexte = {
	subtitle: '',
	inputLabel: '',
	sucheDescription: '',
	auswahlDescription: ''
}

export interface GuiModel {
	readonly texte: KatalogSucheTexte;
	readonly showInputControl: boolean;
	readonly showLoadingIndicator: boolean;
	readonly showAuswahlDescription: boolean;
	readonly katalogantragSuccess: boolean;
}

const initialGuiModel: GuiModel = {
	texte: initialKatalogsucheTexte,
	showInputControl: false,
	showLoadingIndicator: false,
	showAuswahlDescription: false,
	katalogantragSuccess: undefined
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

		const texte: KatalogSucheTexte = getKatalogSucheTexte(state.guiModel.texte,
			katalogtyp,
			[],
			undefined)

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
		const typ = loadedKatalogItems.length > 0 ? loadedKatalogItems[0].typ : state.currentKatalogtyp;

		const texte: KatalogSucheTexte = getKatalogSucheTexte(state.guiModel.texte,
			typ,
			loadedKatalogItems,
			undefined);

		let guiModel = {
			...state.guiModel
			, texte: texte
			, showLoadingIndicator: false
			, showAuswahlDescription: true
		};

		if (loadedKatalogItems.length === 0 || loadedKatalogItems.length > action.immediatelyLoadOnNumberChilds) {

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

		const texte = {
			...state.guiModel.texte,
			auswahlDescription: auswahlDescription
		};

		const guiModel = {
			...state.guiModel,
			texte: texte,
			showLoadingIndicator: false
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

		const texte = getKatalogSucheTexte(state.guiModel.texte,
			katalogtyp,
			[],
			undefined);

		const guiModel = {
			...state.guiModel
			, texte: texte
			, showInputControl: true
			, showLoadingIndicator: false
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
		let katalogtyp = (selectedKatalogItem === undefined) ? state.currentKatalogtyp : selectedKatalogItem.typ;

		let texte = getKatalogSucheTexte(state.guiModel.texte, katalogtyp, [], selectedKatalogItem);

		if (!selectedKatalogItem) {
			const guiModel = {
				...initialGuiModel,
				texte: texte,
				showLoadingIndicator: false
			}

			return { ...state, guiModel: guiModel };
		}

		if (selectedKatalogItem.anzahlKinder > action.immediatelyLoadOnNumberChilds) {
			switch (selectedKatalogItem.typ) {
				case 'LAND': katalogtyp = 'ORT'; break;
				case 'ORT': katalogtyp = 'SCHULE'; break;
				default: break;
			}
			texte = getKatalogSucheTexte(state.guiModel.texte, katalogtyp, [], selectedKatalogItem);
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

		return { ...state, guiModel: guiModel, loadedKatalogItems: [], searchTerm: '', currentKatalogtyp: katalogtyp, selectedKatalogItem: selectedKatalogItem };
	}),

	on(SchulkatalogActions.katalogantragSuccessfullySubmitted, (state, _action) => {

		let guiModel = {
			...state.guiModel,
			katalogantragSuccess: true
		};

		return {...state, guiModel: guiModel};
	})
);

export function reducer(state: SchulkatalogState | undefined, action: Action) {
	return schulkatalogReducer(state, action);
}

// private functions
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

function getKatalogSucheTexte(texte: KatalogSucheTexte, katalogtyp: Katalogtyp, katalogItems: KatalogItem[], selectedKatalogItem: KatalogItem): KatalogSucheTexte {

	let neuerSubtitle: string = '';
	let neuesInputLabel: string = '';
	let neueSucheDescription: string = '';

	if (selectedKatalogItem) {
		switch (selectedKatalogItem.typ) {
			case 'LAND':
				neuerSubtitle = 'Ortssuche';
				neuesInputLabel = 'Ort';
				neueSucheDescription = 'Bitte suchen Sie Ihren Ort (mindestens 3 Buchstaben).';
				break;
			case 'ORT':
				neuerSubtitle = 'Schulsuche';
				neuesInputLabel = 'Schule';
				neueSucheDescription = 'Bitte suchen Sie Ihre Schule (mindestens 3 Buchstaben).';
				break
			default:
				break;
		}
	}

	switch (katalogtyp) {
		case 'LAND':
			neuerSubtitle = 'Ländersuche';
			neuesInputLabel = 'Land';
			neueSucheDescription = 'Bitte geben Sie die Anfangsbuchstaben Ihres Landes ein (mindestens 3 Buchstaben).';
			break;
		case 'ORT':
			neuerSubtitle = 'Ortssuche';
			neuesInputLabel = 'Ort';
			neueSucheDescription = 'Bitte geben Sie die Anfangsbuchstaben Ihres Ortes ein (mindestens 3 Buchstaben).';
			break;
		case 'SCHULE':
			neuerSubtitle = 'Schulsuche';
			neuesInputLabel = 'Schule'
			neueSucheDescription = 'Bitte geben Sie mindestens 3 aufeinanderfolgende Buchstaben Ihres Schulnamens ein.';
			break;
	}

	return {
		...texte,
		subtitle: neuerSubtitle,
		auswahlDescription: getAuswahlDescriptiom(katalogItems),
		inputLabel: neuesInputLabel,
		sucheDescription: neueSucheDescription
	};
}
