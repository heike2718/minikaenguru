import { createAction, props } from '@ngrx/store';
import { Katalogpflegetyp, KatalogpflegeItem, SchulePayload, OrtPayload, LandPayload } from '../katalogpflege.model';
import { SchuleEditorModel } from './katalogpflege.reducer';


export const resetKataloge = createAction(
	'[NavbarComponent] - kataloge login/logout'
);

export const selectKatalogTyp = createAction(
	'[KatalogpflegeFacade] selectKatalogpflegeTyp',
	props<{ typ: Katalogpflegetyp }>()
);

export const sucheFinished = createAction(
	'[KatalogpflegeFacade] searchKatalogItems',
	props<{ typ: Katalogpflegetyp, katalogItems: KatalogpflegeItem[] }>()
);

export const loadLaenderFinished = createAction(
	'[KatalogpflegeFacade] ladeLaender',
	props<{ laender: KatalogpflegeItem[] }>()
);

export const loadChildItemsFinished = createAction(
	'[KatalogpflegeFacade] ladeKinder',
	props<{ parent: KatalogpflegeItem, katalogItems: KatalogpflegeItem[] }>()
);

export const finishedWithError = createAction(
	'[KatalogpflegeFacade] error'
);

export const selectKatalogItem = createAction(
	'[KatalogpflegeFacade] severalMethods',
	props<{ katalogItem: KatalogpflegeItem }>()
);

export const schulePayloadCreated = createAction(
	'[KatalogpflegeFacade] createNeueSchulePayload',
	props<{ schuleEditorModel: SchuleEditorModel }>()
);

export const ortPayloadCreated = createAction(
	'[KatalogpflegeFacade] switchToRenameKatalogItemEditor (ort)',
	props<{ ortPayload: OrtPayload }>()
);

export const editOrtFinished = createAction(
	'[KatalogpflegeFacade] sendRenameOrt',
	props<{ ortPayload: OrtPayload }>()
);

export const landPayloadCreated = createAction(
	'[KatalogpflegeFacade] switchToRenameKatalogItemEditor (land)',
	props<{ landPayload: LandPayload }>()
);

export const editLandFinished = createAction(
	'[KatalogpflegeFacade] sendRenameLand',
	props<{ landPayload: LandPayload }>()
);

export const editSchuleFinished = createAction(
	'[KatalogpflegeFacade] finishEditSchule',
	props<{ schulePayload: SchulePayload }>()
);

export const clearSearchResults = createAction(
	'[KatalogpflegeFacade] clearSearchResults'
);

export const katalogDashboardSelected = createAction(
	'[KatalogpflegeFacade] switchToKataloge'
);


