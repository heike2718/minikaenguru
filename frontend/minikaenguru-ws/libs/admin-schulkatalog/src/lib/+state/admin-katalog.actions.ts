import { createAction, props } from '@ngrx/store';
import { KatalogitemResponseDto, KuerzelResponseDto, Land, Ort, Schule, SchuleEditorModel, SchulePayload } from '../admin-katalog.model';


export const loadLaender = createAction(
	'[AdminSchulkatalog] ladeLaender'
);

export const laenderGeladen = createAction(
    '[AdminSchulkatalog] laenderGeladen',
    props<{ laender: KatalogitemResponseDto[] }>()
);

export const landSelected = createAction(
    '[AdminSchulkatalog] landSelected',
    props<{ land: Land }>()
);

export const landDeselected = createAction(
    '[AdminSchulkatalog] landDeselected'
);


export const loadOrte = createAction(
    '[AdminSchulkatalog] loadOrte',
    props<{land: Land}>()
);

export const findOrte = createAction(
    '[AdminSchulkatalog] findOrte',
    props<{land: Land, suchstring: string}>()
);

export const orteGeladen = createAction(
    '[AdminSchulkatalog] orteGeladen',
    props<{land: Land, orte: KatalogitemResponseDto[]}>()
);

export const ortSelected = createAction(
    '[AdminSchulkatalog] ortSelected',
    props<{ort: Ort}>()
);

export const ortDeselected = createAction(
    '[AdminSchulkatalog]',
);

export const loadSchulen = createAction(
    '[AdminSchulkatalog] loadSchulen',
    props<{ort: Ort}>()
);

export const findSchulen = createAction(
    '[AdminSchulkatalog] findSchulen',
    props<{ort: Ort, suchstring: string}>()
);

export const schuleSelected = createAction(
    '[AdminSchulkatalog] schuleSelected',
    props<{schule: Schule}>()
);

export const schuleDeselected = createAction(
    '[AdminSchulkatalog] schuleDeselected'
);

export const schulenGeladen = createAction(
    '[AdminSchulkatalog] schulenGeladen',
    props<{ort: Ort, schulen: KatalogitemResponseDto[]}>()
);

export const createKuerzel = createAction(
    '[AdminSchulkatalog] createKuerzel'
);

export const kuerzelCreated = createAction(
    '[AdminSchulkatalog] kuerzelCreated',
    props<{kuerzel: KuerzelResponseDto}>()
)

export const startEditSchule = createAction(
    '[AdminSchulkatalog] startEditSchule',
    props<{schuleEditorModel: SchuleEditorModel}>()
);

export const schuleAnlegen = createAction(
    '[AdminSchulkatalog] schuleAnlegen',
    props<{schulePayload: SchulePayload}>()
);

export const schuleUmbenennen = createAction(
    '[AdminSchulkatalog] schuleUmbenennen',
    props<{schulePayload: SchulePayload}>()
);

export const resetState = createAction(
    '[AdminSchulkatalog] resetState'
);
