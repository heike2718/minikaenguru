import { createAction, props } from '@ngrx/store';
import { Katalogtyp, KatalogItem } from '../domain/entities';


export const initKatalogtyp = createAction(
	'[KatalogItemsSucheComponent] initKatalogtyp',
	props<{data: Katalogtyp}>()
);

export const startSearch = createAction(
	'[SchulkatalogFacade] startSearch'
);

export const katalogItemsLoaded = createAction(
	'[SchulkatalogFacade] katalogItemsLoaded',
	props<{data: KatalogItem[]}>()
);

export const searchError = createAction(
	'[KatalogService] searchError',
	props<{data: string}>()
);

export const clearKatalogItems = createAction(
	'[TODO] clearKatalogItems'
);

export const selectKatalogItem = createAction(
	'[KatalogItemComponent] selectKatalogItem',
	props<{data: KatalogItem}>()
);

