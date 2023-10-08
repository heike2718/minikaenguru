import { createAction, props } from '@ngrx/store';
import { Katalogpflegetyp, KatalogpflegeItem, DeprecatedSchulePayload, DeprecatedOrtPayload, DeprecatedLandPayload } from '../katalogpflege.model';
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
	props<{ ortPayload: DeprecatedOrtPayload }>()
);

export const editOrtFinished = createAction(
	'[KatalogpflegeFacade] sendRenameOrt',
	props<{ ortPayload: DeprecatedOrtPayload }>()
);

export const landPayloadCreated = createAction(
	'[KatalogpflegeFacade] switchToRenameKatalogItemEditor (land)',
	props<{ landPayload: DeprecatedLandPayload }>()
);

export const editLandFinished = createAction(
	'[KatalogpflegeFacade] sendRenameLand',
	props<{ landPayload: DeprecatedLandPayload }>()
);

export const editSchuleFinished = createAction(
	'[KatalogpflegeFacade] finishEditSchule',
	props<{ schulePayload: DeprecatedSchulePayload }>()
);

export const clearSearchResults = createAction(
	'[KatalogpflegeFacade] clearSearchResults'
);

export const katalogDashboardSelected = createAction(
	'[KatalogpflegeFacade] switchToKataloge'
);


