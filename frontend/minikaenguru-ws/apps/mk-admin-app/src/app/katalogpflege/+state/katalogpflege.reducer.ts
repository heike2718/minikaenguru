import { Action, createReducer, on } from '@ngrx/store';
import { KatalogpflegeItem, Katalogpflegetyp, mergeKatalogItems, Kataloge, childrenAsArray, SchulePayload, KatalogpflegeItemWithID, OrtPayload, LandPayload, KatalogPflegeItemsMap } from '../katalogpflege.model';
import * as KatalogpflegeActions from './katalogpflege.actions';

export const katalogpflegeFeatureKey = 'mk-admin-app-kataloge';

export interface SchuleEditorModel {
	readonly schulePayload: SchulePayload;
	readonly modusCreate: boolean;
	readonly kuerzelLandDisabled: boolean;
	readonly nameLandDisabled: boolean;
	readonly nameOrtDisabled: boolean;
};

export interface KatalogpflegeState {
	readonly kataloge: Kataloge;
	readonly filteredOrte: KatalogpflegeItem[],
	readonly filteredSchulen: KatalogpflegeItem[],
	readonly selectedKatalogItem: KatalogpflegeItem;
	readonly selectedKatalogTyp: Katalogpflegetyp;
	readonly showLoadingIndicator: boolean;
	readonly sucheBeendet: boolean;
	readonly schuleEditorModel: SchuleEditorModel;
	readonly ortEditorPayload: OrtPayload;
	readonly landEditorPayload: LandPayload;
};

const initialSchuleEditorModelState: SchuleEditorModel = {
	schulePayload: undefined,
	modusCreate: false,
	kuerzelLandDisabled: true,
	nameLandDisabled: true,
	nameOrtDisabled: true
};

const initialState: KatalogpflegeState = {
	kataloge: { laender: [], orte: [], schulen: [] },
	filteredOrte: [],
	filteredSchulen: [],
	selectedKatalogItem: undefined,
	selectedKatalogTyp: undefined,
	showLoadingIndicator: false,
	sucheBeendet: false,
	schuleEditorModel: initialSchuleEditorModelState,
	ortEditorPayload: undefined,
	landEditorPayload: undefined
};

const katalogpflegeReducer = createReducer(initialState,

	on(KatalogpflegeActions.resetKataloge, (_state, _action) => {
		return initialState;
	}),

	on(KatalogpflegeActions.selectKatalogTyp, (state, action) => {

		return { ...state, selectedKatalogTyp: action.typ };

	}),

	on(KatalogpflegeActions.showLoadingIndicator, (state, _action) => {

		return { ...state, showLoadingIndicator: true };
	}),

	on(KatalogpflegeActions.sucheFinished, (state, action) => {

		const alle: KatalogpflegeItem[] = action.katalogItems;
		const kataloge: Kataloge = mergeKatalogItems(alle, state.kataloge);

		let filteredOrte = [];
		let filteredSchulen = [];

		switch (action.typ) {
			case 'ORT': filteredOrte = alle; break;
			case 'SCHULE': filteredSchulen = alle; break;
		}

		return { ...state, kataloge: kataloge, filteredOrte: filteredOrte, filteredSchulen: filteredSchulen, showLoadingIndicator: false };
	}),

	on(KatalogpflegeActions.katalogDashboardSelected, (_state, _action) => {

		return initialState;

	}),

	on(KatalogpflegeActions.loadLaenderFinished, (state, action) => {

		const alle: KatalogpflegeItem[] = action.laender;
		let laenderWithID = state.kataloge.laender !== undefined ? [...state.kataloge.laender] : [];

		alle.forEach(
			item => {
				laenderWithID = new KatalogPflegeItemsMap(laenderWithID).merge(item);
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

		const parent: KatalogpflegeItem = { ...action.parent, kinderGeladen: true };
		let laenderWithID = state.kataloge.laender !== undefined ? [...state.kataloge.laender]: [];
		let orteWithID = state.kataloge.orte !== undefined ? [...state.kataloge.orte] : [];
		const schulenWithID = state.kataloge.schulen !== undefined ? [...state.kataloge.schulen] : [];

		switch (parent.typ) {
			case 'LAND': laenderWithID = new KatalogPflegeItemsMap(laenderWithID).merge(parent); break;
			case 'ORT': orteWithID = new KatalogPflegeItemsMap(orteWithID).merge(parent); break;
		}

		let kataloge: Kataloge = {
			laender: laenderWithID,
			orte: orteWithID,
			schulen: schulenWithID
		};

		const children: KatalogpflegeItem[] = [];


		action.katalogItems.forEach(
			item => {
				children.push({ ...item, parent: parent });
			}
		);

		kataloge = mergeKatalogItems(children, kataloge);
		return { ...state, kataloge: kataloge, showLoadingIndicator: false };
	}),



	on(KatalogpflegeActions.finishedWithError, (state, _action) => {
		return { ...state, showLoadingIndicator: false };
	}),

	on(KatalogpflegeActions.selectKatalogItem, (state, action) => {

		const selectedItem = action.katalogItem;

		let filteredOrte = [];
		let filteredSchulen = [];

		switch (selectedItem.typ) {
			case 'LAND': filteredOrte = childrenAsArray(selectedItem, state.kataloge); break;
			case 'ORT': filteredSchulen = childrenAsArray(selectedItem, state.kataloge); break;
		}


		return { ...state, selectedKatalogItem: selectedItem, selectedKatalogTyp: selectedItem.typ, filteredOrte: filteredOrte, filteredSchulen: filteredSchulen, schuleEditorModel: initialSchuleEditorModelState };
	}),

	on(KatalogpflegeActions.schulePayloadCreated, (state, action) => {

		return { ...state, schuleEditorModel: action.schuleEditorModel, showLoadingIndicator: false };

	}),

	on(KatalogpflegeActions.ortPayloadCreated, (state, action) => {

		return {
			...state,
			schuleEditorModel: initialSchuleEditorModelState,
			ortEditorPayload: action.ortPayload,
			landEditorPayload: undefined,
			showLoadingIndicator: false
		};

	}),

	on(KatalogpflegeActions.landPayloadCreated, (state, action) => {

		return {
			...state,
			schuleEditorModel: initialSchuleEditorModelState,
			ortEditorPayload: undefined,
			landEditorPayload: action.landPayload,
			showLoadingIndicator: false
		};

	}),

	on(KatalogpflegeActions.editSchuleFinished, (state, action) => {

		const schuleEditorModel: SchuleEditorModel = { ...state.schuleEditorModel, schulePayload: action.schulePayload };

		// es ist zu kompliziert, das hier korrekt hineinzumergen. Daher einfach kataloge und filter wieder zurücksetzen.
		// die Länder werden dann beim Navigieren neu geladen, ebenso Orte und Schulen.
		const kataloge: Kataloge = {
			laender: [],
			orte: [],
			schulen: []
		};

		return {
			...state,
			showLoadingIndicator: false,
			sucheBeendet: true,
			schuleEditorModel: schuleEditorModel,
			kataloge: kataloge, filteredOrte: [],
			filteredSchulen: [],
			ortEditorPayload: undefined,
			landEditorPayload: undefined
		};
	}),

	on(KatalogpflegeActions.editOrtFinished, (state, action) => {

		// es ist zu kompliziert, das hier korrekt hineinzumergen. Daher einfach kataloge und filter wieder zurücksetzen.
		// die Länder werden dann beim Navigieren neu geladen, ebenso Orte und Schulen.
		const kataloge: Kataloge = {
			laender: [],
			orte: [],
			schulen: []
		};

		return {
			...state,
			showLoadingIndicator: false,
			sucheBeendet: true,
			schuleEditorModel: initialSchuleEditorModelState,
			kataloge: kataloge, filteredOrte: [],
			filteredSchulen: [],
			ortEditorPayload: action.ortPayload,
			landEditorPayload: undefined
		};
	}),

	on(KatalogpflegeActions.editLandFinished, (state, action) => {

		// es ist zu kompliziert, das hier korrekt hineinzumergen. Daher einfach kataloge und filter wieder zurücksetzen.
		// die Länder werden dann beim Navigieren neu geladen, ebenso Orte und Schulen.
		const kataloge: Kataloge = {
			laender: [],
			orte: [],
			schulen: []
		};

		return {
			...state,
			showLoadingIndicator: false,
			sucheBeendet: true,
			schuleEditorModel: initialSchuleEditorModelState,
			kataloge: kataloge, filteredOrte: [],
			filteredSchulen: [],
			ortEditorPayload: undefined,
			landEditorPayload: action.landPayload
		};
	}),

	on(KatalogpflegeActions.clearSearchResults, (state, _action) => {

		return {
			...state,
			filteredOrte: [],
			filteredSchulen: [],
			selectedKatalogItem: undefined,
			showLoadingIndicator: false,
			sucheBeendet: true
		};
	})
);

export function reducer(state: KatalogpflegeState | undefined, action: Action) {
	return katalogpflegeReducer(state, action);
};

