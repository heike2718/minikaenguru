import { createAction, props } from '@ngrx/store';
import { Anmeldungen } from '../../shared/beteiligungen.model';
import { Wettbewerb } from '../../wettbewerbe/wettbewerb.model';


export const wettbewerbsjahrSelected = createAction(
    '[TeilnahmeDetailsResolver] teilnahmen route selected',
    props<{wettbewerb: Wettbewerb}>()
);

export const loadTeilnhahmen = createAction(
	'[TeilnahmeDetailsResolver] resolve',
	props<{jahr: number}>()
);

export const teilnahmenLoaded = createAction(
	'[TeilnahmenEffects] after Teilnahmen loaded',
	props<{anmeldungen: Anmeldungen}>()
);

export const finishedWithError = createAction(
	'[TeilnahmenFacade] loadTeilnahmen error'
);

export const reset = createAction(
	'[TeilnahmenFacade] reset'
);

