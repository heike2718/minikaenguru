import { createAction, props } from '@ngrx/store';
import { Wettbewerb } from '../wettbewerb.model';



export const aktuellerWettbewerbGeladen = createAction(
	'[TeilnahmenFacade] ladeAktuellenWettbewerb',
	props<{ wettbewerb: Wettbewerb }>()
);

export const reset = createAction(
	'[TeilnahmenFacade] resetState'
);

