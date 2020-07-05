import { createAction, props } from '@ngrx/store';
import { Katalogpflegetyp, KatalogpflegeItem, SchulePayload } from '../katalogpflege.model';


export const resetKataloge = createAction(
	'[NavbarComponent] login'
);

// export const selectKatalogTyp = createAction(
// 	'[KatalogpflegeFacade]',
// 	props<{}>()
// );

export const selectKatalogTyp = createAction(
	'[KatalogpflegeFacade] selectKatalogpflegeTyp',
	props<{typ: Katalogpflegetyp}>()
);

export const startSuche = createAction(
	'[KatalogpflegeFacade] suchen'
);

export const sucheFinished = createAction(
	'[KatalogpflegeFacade]',
	props<{typ: Katalogpflegetyp, katalogItems: KatalogpflegeItem[]}>()
);

export const clearRearchResults = createAction(
	'[KatalogpflegeFacade] clearRearchResults'
);

export const loadLaenderFinished = createAction(
	'[KatalogpflegeFacade] ladeLaender',
	props<{laender: KatalogpflegeItem[]}>()
);

export const loadChildItemsFinished = createAction(
	'[KatalogpflegeFacade] ladeKinder',
	props<{parent: KatalogpflegeItem, katalogItems: KatalogpflegeItem[]}>()
);

export const sucheFinishedWithError = createAction(
	'[KatalogpflegeFacade] error'
);

export const selectKatalogItem = createAction(
	'[KatalogpflegeFacade] prepareEdit',
	props<{katalogItem: KatalogpflegeItem}>()
);

export const neueSchulePayloadCreated = createAction(
	'[KatalogpflegeFacade] createNeueSchulePayload',
	props<{payload: SchulePayload}>()
);

export const editSchulePayloadCreated = createAction(
	'[KatalogpflegeFacade] createEditSchulePayload',
	props<{payload: SchulePayload}>()
);

export const neueSchuleSaved = createAction(
	'[KatalogpflegeFacade] sendCreateSchule',
	props<{katalogItem: KatalogpflegeItem}>()
);

export const editSchuleFinished = createAction(
	'[KatalogpflegeFacade] finishEditSchule'
);

export const resetSelection = createAction(
	'[KatalogpflegeFacade] resetSelection'
);


