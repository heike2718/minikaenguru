import { createAction, props } from '@ngrx/store';
import { Privatteilnahme, Privatveranstalter } from '../../wettbewerb/wettbewerb.model';

export const finishedWithError = createAction(
	'[PrivatveranstalterFacade] on error'
);

export const privatveranstalterGeladen = createAction(
	'[PrivatveranstalterFacade] ladePrivatveranstalter',
	props<{ veranstalter: Privatveranstalter }>()
);

export const privatveranstalterAngemeldet = createAction(
	'[PrivatveranstalterFacade] privatveranstalterAnmelden',
	props<{ teilnahme: Privatteilnahme }>()
);

export const aboNewsletterChanged = createAction(
	'[PrivatveranstalterFacade] changeAboNewsletter'
);

export const resetPrivatveranstalter = createAction(
	'[PrivatveranstalterFacade] resetState'
);

