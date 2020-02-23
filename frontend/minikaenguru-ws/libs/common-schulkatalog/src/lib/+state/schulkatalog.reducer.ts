import { Action, createReducer, on } from '@ngrx/store';
import { Katalogtyp, InverseKatalogItem } from '../domain/entities';
import * as SchulkatalogActions from './schulkatalog.actions';

export const schulkatalogFeatureKey = 'schulkatalog';

export interface SchulkatalogState {
  currentKatalogtyp: Katalogtyp,
  loadedKatalogItems: InverseKatalogItem[],
  selectedKatalogItem: InverseKatalogItem,
  loadingKatalogItems: boolean,
  error: string
}

export const initialState: SchulkatalogState = {
  currentKatalogtyp: undefined,
  loadedKatalogItems: [],
  selectedKatalogItem: undefined,
  loadingKatalogItems: false,
  error: undefined
};

const schulkatalogReducer = createReducer(
  initialState,

  on(SchulkatalogActions.initKatalogtyp, (state, action) => {
	  return {...state, loadedKatalogItems: [], currentKatalogtyp: action.data, selectedKatalogItem: undefined, loadingKatalogItems: false, error: undefined};
  }),
  on(SchulkatalogActions.startSearch, (state, _action) => {
	  return {...state, loadingKatalogItems: true}
  }),
  on(SchulkatalogActions.katalogItemsLoaded, (state, action) => {
	  const loadedKatalogItems = action.data;
	  return {...state, loadedKatalogItems, loadingKatalogItems: false, selectedKatalogItem: undefined, error: undefined}
  }),
  on(SchulkatalogActions.clearKatalogItems, (state, _action) => {
	  return {...state, loadedKatalogItems: [], loadingKatalogItems: false, selectedKatalogItem: undefined, error: undefined}
  }),
  on(SchulkatalogActions.searchError, (state, action) => {
	  return {...state, loadedKatalogItems: [], loadingKatalogItems: false, selectedKatalogItem: undefined, error: action.data}
  }),
  on(SchulkatalogActions.selectKatalogItem, (state, action) => {
	  return {...state, loadingKatalogItems: false, selectedKatalogItem: action.data, error: undefined}
  })
);

export function reducer(state: SchulkatalogState | undefined, action: Action) {
  return schulkatalogReducer(state, action);
}
