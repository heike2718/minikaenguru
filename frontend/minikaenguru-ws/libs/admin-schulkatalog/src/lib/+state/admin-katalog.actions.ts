import { createAction, props } from '@ngrx/store';
import { KatalogitemResponseDto, KuerzelResponseDto, Land, LandPayload, Ort, OrtPayload, Schule, SchuleEditorModel, SchulePayload } from '../admin-katalog.model';

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

export const createSchule = createAction(
    '[AdminSchulkatalog] createSchule',
    props<{schulePayload: SchulePayload}>()
);

export const updateSchule = createAction(
    '[AdminSchulkatalog] updateSchule',
    props<{schulePayload: SchulePayload}>()
);

export const createSchuleSuccess = createAction(
    '[AdminSchulkatalog] createSchuleSuccess',
    props<{schulePayload: SchulePayload}>()
);

export const updateSchuleSuccess = createAction(
    '[AdminSchulkatalog] updateSchuleSuccess',
    props<{schulePayload: SchulePayload}>()
);

export const startEditOrt = createAction(
    '[AdminSchulkatalog] startEditOrt',
    props<{ortPayload: OrtPayload}>()
);

export const updateOrt = createAction(
    '[AdminSchulkatalog] updateOrt',
    props<{ortPayload: OrtPayload}>()
);

export const updateOrtSuccess = createAction(
    '[AdminSchulkatalog] updateOrtSuccess',
    props<{ortPayload: OrtPayload}>()
);

export const startEditLand = createAction(
    '[AdminSchulkatalog] startEditLand',
    props<{landPayload: LandPayload}>()
);

export const updateLand = createAction(
    '[AdminSchulkatalog] updateLand',
    props<{landPayload: LandPayload}>()
);

export const updateLandSuccess = createAction(
    '[AdminSchulkatalog] updateLandSuccess',
    props<{landPayload: LandPayload}>()
);

export const resetState = createAction(
    '[AdminSchulkatalog] resetState'
);
