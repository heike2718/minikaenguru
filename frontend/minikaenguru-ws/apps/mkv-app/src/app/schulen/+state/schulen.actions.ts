import { createAction, props } from '@ngrx/store';
import { Schule } from '../schulen.model';

export const schulenLoaded = createAction(
	'[SchulenFacade] loadSchulen',
	props<{ schulen: Schule[] }>()
);

export const startLoading = createAction(
	'[SchulenFacade] before request'
);

export const finishedWithError = createAction(
	'[SchulenFacade] on error'
);

export const schuleDetailsLoaded = createAction(
	'[SchulenFacade] loadDetails subscription',
	props<{ schule: Schule }>()
);

export const restoreDetailsFromCache = createAction(
	'[SchulenFacade] restoreDetailsFromCache',
	props<{ kuerzel: string }>()
)

export const selectSchule = createAction(
	'[SchulenFacade] selectSchule',
	props<{ schule: Schule }>()
);

export const deselectSchule = createAction(
	'[SchulenFacade] resetSelection'
);

export const resetSchulen = createAction(
	'[SchulenFacade] resetState()'
);

