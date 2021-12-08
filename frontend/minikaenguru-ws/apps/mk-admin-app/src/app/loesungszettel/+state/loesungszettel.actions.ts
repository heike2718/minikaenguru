import { createAction, props } from '@ngrx/store';
import { Loesungszettel } from '../loesungszettel.model';

export const startLoading = createAction(
    '[LoesungszettelFacade] start backendcall'
);

export const anzahlLoesungszettelLoaded = createAction(
    '[LoesungszettelFacade] getAnzahl',
    props<{size: number}>()
);

export const backendCallFinishedWithError = createAction(
    '[LoesungszettelFacade] on error'
);

export const loesungszettelLoaded = createAction(
    '[LoesungszettelFacade] loadPage',
    props<{loesungszettel: Loesungszettel[]}>()
);

export const clearLoesungszettel = createAction(
    '[LoesungszettelFacade] reset'
);