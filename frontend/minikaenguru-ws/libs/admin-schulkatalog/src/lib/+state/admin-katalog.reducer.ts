import { Action, createReducer, on } from '@ngrx/store';
import { Land, Ort, Schule, mapToLand, mapToOrtInLand, mapToSchuleInOrt } from "../admin-katalog.model";
import * as AdminSchulkatalogActions from './admin-katalog.actions';

export const adminSchulkatalogFeatureKey = 'mk-admin-app-schulkatalog';

export interface AdminSchulkatalogState {
    readonly laender: Land[];
    readonly selectedLand: Land | undefined;
    readonly orte: Ort[];
    readonly selectedOrt: Ort | undefined;
    readonly schulen: Schule[];
    readonly selectedSchule: Schule | undefined;

};

const initialAdminSchulkatalogState: AdminSchulkatalogState = {
    laender: [],
    selectedLand: undefined,
    orte: [],
    selectedOrt: undefined,
    schulen: [],
    selectedSchule: undefined
};

const adminSchulkatalogReducer = createReducer(initialAdminSchulkatalogState,

    on(AdminSchulkatalogActions.laenderGeladen, (_state, action) => {

        return {
            ...initialAdminSchulkatalogState,
            laender: action.laender.map((item) => mapToLand(item))
        };
    }),

    on(AdminSchulkatalogActions.landSelected, (state, action) => {

        return {
            ...state,
            selectedLand: action.land,
            orte: [],
            selectedOrt: undefined,
            schulen: [],
            selectedSchule: undefined
        };
    }),

    on(AdminSchulkatalogActions.landDeselected, (state, _action) => {

        return {
            ...state,
            selectedLand: undefined,
            orte: [],
            selectedOrt: undefined,
            schulen: [],
            selectedSchule: undefined
        };
    }),

    on(AdminSchulkatalogActions.orteGeladen, (state, action) => {

        const orte: Ort[] = action.orte.map(o => mapToOrtInLand(action.land, o));

        return {
            ...state,
            orte: orte,
            selectedOrt: undefined,
            schulen: [],
            selectedSchule: undefined
        };

    }),

    on(AdminSchulkatalogActions.ortSelected, (state, action) => {
        return {
            ...state,
            selectedOrt: action.ort,
            schulen: [],
            selectedSchule: undefined
        };
    }),

    on(AdminSchulkatalogActions.ortDeselected, (state, action) => {
        return {
            ...state,
            selectedOrt: undefined,
            schulen: [],
            selectedSchule: undefined
        };
    }),

    on(AdminSchulkatalogActions.schulenGeladen, (state, action) => {

        const schulen: Schule[] = action.schulen.map(s => mapToSchuleInOrt(action.ort, s));

        return {
            ...state,
            schulen: schulen,
            selectedSchule: undefined
        };

    }),

    on(AdminSchulkatalogActions.schuleSelected, (state, action) => {
        return {
            ...state,
            selectedSchule: action.schule
        };
    }),

    on(AdminSchulkatalogActions.schuleDeselected, (state, _action) => {
        return {
            ...state,
            selectedSchule: undefined
        };
    }),

    on(AdminSchulkatalogActions.resetState, (_state, _action) => initialAdminSchulkatalogState)
);


export function reducer(state: AdminSchulkatalogState | undefined, action: Action) {
    return adminSchulkatalogReducer(state, action);
};


