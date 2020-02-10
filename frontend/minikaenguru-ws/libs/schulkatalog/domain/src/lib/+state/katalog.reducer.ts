import { Action, createReducer, on } from '@ngrx/store';
import * as KatalogActions from './katalog.actions';
import { KatalogItem } from '../entities/entities';

export const katalogFeatureKey = 'katalog';

export interface KatalogState {
  laender: KatalogItem[],
  orte: KatalogItem[],
  schulen: KatalogItem[],
  selectedLand: KatalogItem,
  selectedOrt: KatalogItem,
  selectedSchule: KatalogItem
}

export const initialState: KatalogState = {
  laender: [],
  orte: [],
  schulen: [],
  selectedLand: undefined,
  selectedOrt: undefined,
  selectedSchule: undefined
};

const katalogReducer = createReducer(
  initialState,

  on(KatalogActions.loadKatalogs, state => state),
  on(KatalogActions.loadKatalogsSuccess, (state, action) => state),
  on(KatalogActions.loadKatalogsFailure, (state, action) => state),

);

export function reducer(state: KatalogState | undefined, action: Action) {
  return katalogReducer(state, action);
}
