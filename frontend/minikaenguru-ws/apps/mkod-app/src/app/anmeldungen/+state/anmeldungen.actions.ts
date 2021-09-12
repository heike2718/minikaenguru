import { createAction, props } from '@ngrx/store';
import { Anmeldungen } from '../../shared/beteiligungen.model';




export const loadAnmeldungen = createAction(
	'[AnmeldungenFacade] loadAll'
);

export const anmeldungenLoaded = createAction(
	'[AnmeldungenFacade] loadAll finished',
	props<{anmeldungen: Anmeldungen}>()
);

export const finishedWithError = createAction(
	'[AnmeldungenFacade] loadAnmeldungen error'
);


export const reset = createAction(
	'[AnmeldungenFacade] reset'
);

