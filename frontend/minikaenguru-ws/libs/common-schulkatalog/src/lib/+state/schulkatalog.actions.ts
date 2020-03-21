import { createAction, props } from '@ngrx/store';
import { Katalogtyp, KatalogItem } from '../domain/entities';

export const initSucheComponentCompleted = createAction(
	'[KatalogItemsSucheComponent] initSucheComponentCompleted',
	props<{katalogtyp: Katalogtyp}>()
)

export const startSearch = createAction(
	'[KatalogItemsSucheComponent] startSearch',
	props<{katalogItem: KatalogItem, searchTerm: string}>()
);

export const searchFinished = createAction(
	'[SchulkatalogFacade] searchFinished',
	props<{katalogItems: KatalogItem[]}>()
);

export const searchError = createAction(
	'[SchulkatalogFacade] searchError'
);

export const startLoadChildItems = createAction(
	'[SchulkatalogFacade] startLoadChildItems'
);

export const childItemsLoaded = createAction(
	'[SchulkatalogFacade] childItemsLoaded',
	props<{katalogItems: KatalogItem[]}>()
);

export const katalogItemSelected = createAction(
	'[KatalogItemComponent] katalogItemSelected',
	props<{katalogItem: KatalogItem}>()
);


