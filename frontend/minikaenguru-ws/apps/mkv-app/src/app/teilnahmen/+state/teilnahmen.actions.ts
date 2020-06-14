import { createAction, props } from '@ngrx/store';
import { Wettbewerb } from '../teilnahmen.model';


export const aktuellerWettbewerbGeladen = createAction(
	'[TeilnahmenFacade] ladeAktuellenWettbewerb',
	props<{ wettbewerb: Wettbewerb }>()
);

export const zugangsstatusUnterlagenGeladen = createAction(
	'[TeilnahmenFacade] ladeZugangsstatus',
	props<{ hatZugang: boolean }>()
);

export const reset = createAction(
	'[TeilnahmenFacade] resetState'
);


