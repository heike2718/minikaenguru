import { createAction, props } from '@ngrx/store';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';

export const teilnahmenummerSelected = createAction(

	'[TeilnahmenFacade] selectTeilnahmenummer',
	props<{teilnahmenummer: string, nameSchule: string}>()

);

export const startLoading = createAction(
	'[TeilnahmenFacade] ladeAnonymisierteTeilnahmen - start'
);

export const loadFinishedWithError = createAction(
	'[TeilnahmenFacade] ladeAnonymisierteTeilnahmen - error'
);

export const anonymeTeilnahmenGeladen = createAction(
	'[TeilnahmenFacade] ladeAnonymisierteTeilnahmen - finished',
	props<{ anonymeTeilnahmen: AnonymisierteTeilnahme[] }>()
);

export const resetState = createAction(
	'[LogoutService] logout reset Teilnahmen-State'
);


