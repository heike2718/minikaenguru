import { createAction, props } from '@ngrx/store';
import { Wettbewerb, AbstractVeranstalter } from '../wettbewerb.model';



export const aktuellerWettbewerbGeladen = createAction(
	'[TeilnahmenFacade] ladeAktuellenWettbewerb',
	props<{ wettbewerb: Wettbewerb }>()
);

export const reset = createAction(
	'[TeilnahmenFacade] resetState'
);

export const veranstalterLoaded = createAction(
	'[LehrerFacade/PrivatveranstalterFacade] load veranstalter',
	props<{veranstalter: AbstractVeranstalter}>()
);



