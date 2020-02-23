import { createAction, props } from '@ngrx/store';
import { Katalogtyp, InverseKatalogItem } from '../domain/entities';


export const initKatalogtyp = createAction(
	'[KatalogItemsSucheComponent] initKatalogtyp',
	props<{data: Katalogtyp}>()
);

export const startSearch = createAction(
	'[SchulkatalogFacade] startSearch'
);

export const katalogItemsLoaded = createAction(
	'[SchulkatalogFacade] katalogItemsLoaded',
	props<{data: InverseKatalogItem[]}>()
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
	props<{data: InverseKatalogItem}>()
);

