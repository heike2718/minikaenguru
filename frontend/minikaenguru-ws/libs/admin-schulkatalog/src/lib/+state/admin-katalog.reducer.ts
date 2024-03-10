import { Action, createReducer, on } from '@ngrx/store';
import { KuerzelResponseDto, Land, LandPayload, Ort, OrtPayload, Schule, SchuleEditorModel, SchulePayload, initialSchuleEditorModel, initialSchulePayload, mapToLand, mapToOrtInLand, mapToSchuleInOrt } from "../admin-katalog.model";
import * as AdminSchulkatalogActions from './admin-katalog.actions';
import { ortPayload } from './admin-katalog.selectors';

export const adminSchulkatalogFeatureKey = 'mk-admin-app-schulkatalog';

export interface AdminSchulkatalogState {
    readonly laender: Land[];
    readonly selectedLand: Land | undefined;
    readonly orte: Ort[];
    readonly selectedOrt: Ort | undefined;
    readonly schulen: Schule[];
    readonly selectedSchule: Schule | undefined;
    readonly kuerzel: KuerzelResponseDto | undefined;
    readonly landPayload: LandPayload | undefined;
    readonly ortPayload: OrtPayload | undefined;
    readonly schuleEditorModel: SchuleEditorModel | undefined;
};

const initialAdminSchulkatalogState: AdminSchulkatalogState = {
    laender: [],
    selectedLand: undefined,
    orte: [],
    selectedOrt: undefined,
    schulen: [],
    selectedSchule: undefined,
    kuerzel: undefined,
    landPayload: undefined,
    ortPayload: undefined,
    schuleEditorModel: undefined
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

    on(AdminSchulkatalogActions.kuerzelCreated, (state, action) => {
        return {
            ...
            state,
            kuerzel: action.kuerzel
        };
    }),

    on(AdminSchulkatalogActions.startEditSchule, (state, action) => {

        return {
            ...state,
            kuerzel: undefined,
            schuleEditorModel: action.schuleEditorModel
        }
    }),

    on(AdminSchulkatalogActions.startEditOrt, (state, action) => {

        return {
            ...state,
            ortPayload: action.ortPayload
        }

    }),

    on(AdminSchulkatalogActions.startEditLand, (state, action) => {

        return {
            ...state,
            landPayload: action.landPayload
        }

    }),

    on(AdminSchulkatalogActions.updateLandSuccess, (state, action) => {

        if (state.landPayload) {
            return {
                ...state,
                landPayload: action.landPayload
            };
        } else {
            return { ...state };
        }
    }),

    on(AdminSchulkatalogActions.updateOrtSuccess, (state, action) => {

        if (state.ortPayload) {

            return {
                ...state,
                ortPayload: action.ortPayload
            };
        } else {
            return { ...state };
        }
    }),

    on(AdminSchulkatalogActions.updateSchuleSuccess, (state, action) => {

        if (state.schuleEditorModel) {
            const schulePayload: SchulePayload = action.schulePayload;
            const schuleEditorModel: SchuleEditorModel = { ...state.schuleEditorModel, schulePayload: schulePayload };

            return {
                ...state,
                schuleEditorModel: schuleEditorModel
            };
        } else {
            return { ...state };
        }
    }),

    on(AdminSchulkatalogActions.resetState, (_state, _action) => initialAdminSchulkatalogState)
);


export function reducer(state: AdminSchulkatalogState | undefined, action: Action) {
    return adminSchulkatalogReducer(state, action);
};


