import {
  ActionReducerMap,
  MetaReducer
} from '@ngrx/store';
import { routerReducer } from '@ngrx/router-store';

export interface AppState {
};

export const reducers: ActionReducerMap<AppState> = {

	router: routerReducer

};


export const metaReducers: MetaReducer<AppState>[] = [];
