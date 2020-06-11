import { createAction, props } from '@ngrx/store';
import { Schule, SchuleDashboardModel } from '../schulen.model';

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
	props<{ schule: Schule, details: SchuleDashboardModel }>()
);

export const selectSchule = createAction(
	'[SchuleCardComponent] select schule',
	props<{ schule: Schule }>()
);

export const unselectSchule = createAction(
	'[SchuleCardComponent] unselect schule'
);

export const resetSchulen = createAction(
	'[SchulenFacade] resetState()'
);

