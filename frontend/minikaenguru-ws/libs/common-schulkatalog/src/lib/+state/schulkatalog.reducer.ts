import { Action, createReducer, on } from '@ngrx/store';
import { Katalogtyp, KatalogItem } from '../domain/entities';
import * as SchulkatalogActions from './schulkatalog.actions';

export const schulkatalogFeatureKey = 'schulkatalog';

export interface SchulkatalogState {
	currentKatalogtyp: Katalogtyp,
	loadedKatalogItems: KatalogItem[],
	searchTerm: string,
	selectedKatalogItem: KatalogItem,
	loadingKatalogItems: boolean,
	error: string
}

export const initialState: SchulkatalogState = {
	currentKatalogtyp: undefined,
	loadedKatalogItems: [],
	searchTerm: '',
	selectedKatalogItem: undefined,
	loadingKatalogItems: false,
	error: undefined
};

const schulkatalogReducer = createReducer(
	initialState,

	on(SchulkatalogActions.initKatalogtyp, (_state, action) => {
		return { ...initialState, currentKatalogtyp: action.data };
	}),

	on(SchulkatalogActions.startSearch, (state, _action) => {
		return { ...state, loadingKatalogItems: true }
	}),

	on(SchulkatalogActions.katalogItemsLoaded, (state, action) => {
		const loadedKatalogItems = action.data;

		return { ...state
			, loadedKatalogItems
			, searchTerm: ''
			, loadingKatalogItems: false
			, selectedKatalogItem: undefined
			, error: undefined }
	}),

	on(SchulkatalogActions.clearKatalogItems, (state, _action) => {
		return { ...state, loadedKatalogItems: [], loadingKatalogItems: false, selectedKatalogItem: undefined, error: undefined }
	}),
	on(SchulkatalogActions.searchError, (state, action) => {
		return { ...state, loadedKatalogItems: [], loadingKatalogItems: false, selectedKatalogItem: undefined, error: action.data }
	}),
	on(SchulkatalogActions.selectKatalogItem, (state, action) => {
		return { ...state, loadingKatalogItems: false, selectedKatalogItem: action.data, error: undefined }
	})
);

export function reducer(state: SchulkatalogState | undefined, action: Action) {
	return schulkatalogReducer(state, action);
}
