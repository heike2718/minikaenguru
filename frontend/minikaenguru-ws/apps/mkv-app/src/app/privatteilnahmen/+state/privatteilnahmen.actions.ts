import { createAction, props } from '@ngrx/store';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';

export const startLoading = createAction(
	'[PrivatteilnahmenFacade] ladeAnonymisierteTeilnahmen - start'
);

export const loadFinishedWithError = createAction(
	'[PrivatteilnahmenFacade] ladeAnonymisierteTeilnahmen - error'
);

export const anonymeTeilnahmenGeladen = createAction(
	'[PrivatteilnahmenFacade] ladeAnonymisierteTeilnahmen - finished',
	props<{ anonymeTeilnahmen: AnonymisierteTeilnahme[] }>()
);


