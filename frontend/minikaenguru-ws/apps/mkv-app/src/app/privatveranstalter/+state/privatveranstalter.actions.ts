import { createAction, props } from '@ngrx/store';
import { Privatteilnahme, Privatveranstalter, AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';


export const startLoading = createAction(
	'[PrivatveranstalterFacade] before request'
);

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
)

export const resetPrivatveranstalter = createAction(
	'[PrivatveranstalterFacade] resetState'
);

