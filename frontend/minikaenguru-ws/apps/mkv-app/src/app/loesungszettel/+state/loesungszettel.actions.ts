import { createAction, props } from '@ngrx/store';
import { Loesungszettel } from '../loesungszettel.model';


export const createNewLoesungsettel = createAction(
	'[LoesungszettelFacade] createNewLoesungszettel',
	props<{loesungszettel: Loesungszettel}>()
);

export const startLoading = createAction(
	'[LoesungszettelFacade] diverse bevore backend call'
);

export const finishedWithError = createAction(
	'[LoesungszettelFacade] diverse on error'
);

export const loesungszettelLoaded = createAction(
	'[LoesungszettelFacade] loadLoesungszettel',
	props<{loesungszettel: Loesungszettel}>()
);

export const loesungszettelSelected = createAction(
	'[LoesungszettelFacade] selectLoesungszettel',
	props<{loesungszettel: Loesungszettel}>()
);

export const resetModule = createAction(
	'[LoesungszettelFacade] resetState'
);
