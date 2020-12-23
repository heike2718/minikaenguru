import { createAction, props } from '@ngrx/store';
import { Loesungszettel, Loesungszettelzeile, LoesungszettelResponseModel } from '../loesungszettel.model';
import { LoesungszettelPunkte } from '@minikaenguru-ws/common-components';


export const newLoesungszettelCreated = createAction(
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

export const loesungszettelChanged = createAction(
	'[LoesungszettelFacade] loesungszettelChanged',
	props<{zeile: Loesungszettelzeile}>()
);

export const editLoesungszettelCancelled = createAction(
	'[LoesungszettelFacade] cancelEditLoesungszettel'
);

export const loesungszettelSaved = createAction(
	'[LoesungszettelFacade] saveLoesungszettel',
	props<{loesungszettel: Loesungszettel}>()
)


export const resetModule = createAction(
	'[LoesungszettelFacade] resetState'
);
