import { createAction, props } from '@ngrx/store';
import { Katalogtyp, KatalogItem } from '../domain/entities';

export const initSchulkatalog = createAction(
	'[SchulkatalogFacade] initSchulkatalog',
	props<{ katalogtyp: Katalogtyp}>()
);

export const startSearch = createAction(
	'[KatalogItemsSucheComponent] startSearch',
	props<{ selectedKatalogtyp: Katalogtyp, selectedItem: KatalogItem, searchTerm: string }>()
);

export const searchFinished = createAction(
	'[SchulkatalogFacade] searchFinished',
	props<{ katalogItems: KatalogItem[], immediatelyLoadOnNumberChilds: number }>()
);

export const searchError = createAction(
	'[SchulkatalogFacade] searchError'
);

export const startLoadChildItems = createAction(
	'[SchulkatalogFacade] startLoadChildItems'
);

export const childItemsLoaded = createAction(
	'[SchulkatalogFacade] childItemsLoaded',
	props<{ katalogItems: KatalogItem[] }>()
);

export const katalogItemSelected = createAction(
	'[SchulkatalogFacade] selectKatalogItem',
	props<{ katalogItem: KatalogItem, immediatelyLoadOnNumberChilds: number }>()
);

export const katalogantragSuccessfullySubmitted = createAction(
	'[InternalFacade] submitSchulkatalogAntrag'
)



