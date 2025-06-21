import { createAction, props } from '@ngrx/store';
import { Loesungszettel } from '../loesungszettel.model';
import { LoesungszettelResponse, Loesungszettelzeile } from '@minikaenguru-ws/common-components';


export const newLoesungszettelCreated = createAction(
	'[LoesungszettelFacade] createNewLoesungszettel',
	props<{loesungszettel: Loesungszettel}>()
);

export const startLoading = createAction(
	'[LoesungszettelFacade] diverse before backend call'
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

export const loesungszettelSaved = createAction(
	'[LoesungszettelFacade] saveLoesungszettel',
	props<{loesungszettelAlt:Loesungszettel, loesungszettelNeu: LoesungszettelResponse}>()
);

export const editLoesungszettelCancelled = createAction(
	'[LoesungszettelFacade] cancelEditLoesungszettel'
);

export const loesungszettelDeleted = createAction(
	'[LoesungszettelFacade] deleteLoesungszettel',
	props<{loesungszettel: Loesungszettel}>()
);

export const kindDeleted = createAction(
	'[KinderFacade] deleteKindSuccess',
	props<{kindUuid: string}>()
);

export const resetModule = createAction(
	'[LoesungszettelFacade] resetState'
);
