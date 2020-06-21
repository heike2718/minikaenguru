import { createAction, props } from '@ngrx/store';
import { Privatveranstalter } from '../privatveranstalter.model';
import { Privatteilnahme } from '../../wettbewerb/wettbewerb.model';


export const privatveranstalterGeladen = createAction(
	'[PrivatveranstalterFacade] ladePrivatveranstalter',
	props<{ veranstalter: Privatveranstalter }>()
);

export const resetPrivatveranstalter = createAction(
	'[PrivatveranstalterFacade] resetState'
);

export const privatteilnahmeCreated = createAction(
	'[PrivatveranstalterFacade] createTeilnahme',
	props<{ teilnahme: Privatteilnahme }>()
);

