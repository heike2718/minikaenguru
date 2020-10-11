import { createAction, props } from '@ngrx/store';
import { Schule } from './../schulen/schulen.model';
import { Schulteilnahme, Lehrer } from '../../wettbewerb/wettbewerb.model';


export const datenLehrerGeladen = createAction(
	'[LehrerFacade] loadLehrer',
	props<{lehrer: Lehrer}>()
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

export const vertragAdvCreated= createAction(
	'[VertragAdvFacade] submitVertrag success',
	props<{schulkuerzel: string}>()
);

export const restoreDetailsFromCache = createAction(
	'[LehrerFacade] restoreDetailsFromCache',
	props<{ kuerzel: string }>()
);

export const selectSchule = createAction(
	'[LehrerFacade] selectSchule',
	props<{ schule: Schule }>()
);

export const deselectSchule = createAction(
	'[LehrerFacade] resetSelection'
);

export const schuleAngemeldet = createAction(
	'[LehrerFacade] schuleAnmelden',
	props<{ teilnahme: Schulteilnahme, angemeldetDurch: string}>()
);

export const aboNewsletterChanged = createAction(
	'[LehrerFacade] changeAboNewsletter'
);

export const resetLehrer = createAction(
	'[LehrerFacade] resetState()'
);

