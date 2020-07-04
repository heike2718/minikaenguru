import { createAction, props } from '@ngrx/store';
import { Katalogpflegetyp, KatalogpflegeItem } from '../katalogpflege.model';


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
	props<{katalogItems: KatalogpflegeItem[]}>()
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

export const resetSelection = createAction(
	'[KatalogpflegeFacade] resetSelection'
);


