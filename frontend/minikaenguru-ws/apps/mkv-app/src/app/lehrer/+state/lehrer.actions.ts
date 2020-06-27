import { createAction, props } from '@ngrx/store';
import { Schule } from './../schulen/schulen.model';
import { Lehrer } from '../lehrer.model';


export const zugangsstatusUnterlagenGeladen = createAction(
	'[LehrerFacade] ladeLehrer',
	props<{hatZugang: boolean}>()
);

export const schulenLoaded = createAction(
	'[LehrerFacade] loadSchulen',
	props<{ schulen: Schule[] }>()
);

export const startLoading = createAction(
	'[LehrerFacade] before request'
);

export const finishedWithError = createAction(
	'[LehrerFacade] on error'
);

export const schuleDetailsLoaded = createAction(
	'[LehrerFacade] loadDetails subscription',
	props<{ schule: Schule }>()
);

export const restoreDetailsFromCache = createAction(
	'[LehrerFacade] restoreDetailsFromCache',
	props<{ kuerzel: string }>()
)

export const selectSchule = createAction(
	'[LehrerFacade] selectSchule',
	props<{ schule: Schule }>()
);

export const deselectSchule = createAction(
	'[LehrerFacade] resetSelection'
);

export const resetLehrer = createAction(
	'[LehrerFacade] resetState()'
);

