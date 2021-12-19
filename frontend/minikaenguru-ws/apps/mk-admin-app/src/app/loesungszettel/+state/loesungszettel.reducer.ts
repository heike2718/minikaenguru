import { Action, createReducer, on } from '@ngrx/store';
import { Loesungszettel, LoesungszettelPage, LoesungszettelPageMap } from "../loesungszettel.model";
import * as LoesungszettelActions from './loesungszettel.actions';

export const loesungszettelFeatureKey = 'mk-admin-app-loesungszettel';

export interface LoesungszettelState {
    readonly loading: boolean;
    readonly selectedJahr?: number;
    readonly anzahlLoesungszettel: number;
    readonly pages: LoesungszettelPage[];
    readonly pageContent: Loesungszettel[];
};

const initialLoesungszettelState: LoesungszettelState = {
    loading: false,
    selectedJahr: undefined,
    anzahlLoesungszettel: 0,
    pages: [],
    pageContent: []
};

const loesungszettelReducer = createReducer(initialLoesungszettelState,

    on(LoesungszettelActions.startLoading, (state, _action) => {
        return {...state, loading: true};
    }),

    on(LoesungszettelActions.backendCallFinishedWithError, (state, _action) => {
        return {...state, loading: false};
    }),

    on(LoesungszettelActions.jahrSelected, (state, action) => {

        if (state.selectedJahr === action.jahr) {

            return {...state, selectedJahr: action.jahr};
        } else {

            return {...initialLoesungszettelState, selectedJahr: action.jahr};
        }
    }),

    on(LoesungszettelActions.anzahlLoesungszettelLoaded, (state, action) => {

        return {...state, loading: false, anzahlLoesungszettel: action.size};
    }),

    on(LoesungszettelActions.loesungszettelLoaded, (state, action) => {

       const newPage: LoesungszettelPage = {pageNumber: action.pageNumber, content: action.loesungszettel};
       const zettelPages: LoesungszettelPage[] = new LoesungszettelPageMap(state.pages).merge(newPage);
       return {...state, loading: false, pages: zettelPages, pageContent: action.loesungszettel};
    }),

    on(LoesungszettelActions.resetLoesungszettel, (_state, _action) => {
        return initialLoesungszettelState;
    }),

);

export function reducer(state: LoesungszettelState | undefined, action: Action) {

    return loesungszettelReducer(state, action);
}
