import { Action, createReducer, on } from '@ngrx/store';
import { Loesungszettel, LoesungszettelMap, LoesungszettelWithID } from "../loesungszettel.model";
import * as LoesungszettelActions from './loesungszettel.actions';

export const loesungszettelFeatureKey = 'mk-admin-app-loesungszettel';

export interface LoesungszettelState {
    readonly loading: boolean;
    readonly anzahlLoesungszettel: number;
    readonly loesungszettelMap: LoesungszettelWithID[];
    readonly pageContent: Loesungszettel[];
};

const initialLoesungszettelState: LoesungszettelState = {
    loading: false,
    anzahlLoesungszettel: 0,
    loesungszettelMap: [],
    pageContent: []
};

const loesungszettelReducer = createReducer(initialLoesungszettelState,

    on(LoesungszettelActions.startLoading, (state, _action) => {
        return {...state, loading: true};
    }),

    on(LoesungszettelActions.backendCallFinishedWithError, (state, _action) => {
        return {...state, loading: false};
    }),

    on(LoesungszettelActions.anzahlLoesungszettelLoaded, (state, action) => {

        return {...state, loading: false, anzahlLoesungszettel: action.size};
    }),

    on(LoesungszettelActions.loesungszettelLoaded, (state, action) => {

       const zettelMap: LoesungszettelWithID[] = new LoesungszettelMap(state.loesungszettelMap).merge(action.loesungszettel);
       return {...state, loading: false, loesungszettelMap: zettelMap, pageContent: action.loesungszettel};
    }),

    on(LoesungszettelActions.clearLoesungszettel, (_state, _action) => {
        return initialLoesungszettelState;
    }),

);

export function reducer(state: LoesungszettelState | undefined, action: Action) {

    return loesungszettelReducer(state, action);
}
