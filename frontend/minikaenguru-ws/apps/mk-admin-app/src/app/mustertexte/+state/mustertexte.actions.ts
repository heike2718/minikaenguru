import { createAction, props } from '@ngrx/store';
import { Mustertext } from '../../shared/shared-entities.model';


export const startBackendCall = createAction(
	'[MustertexteFacade] diverse'
);

export const backendCallFinishedWithError = createAction(
	'[MustertexteFacade] errors'
);

export const mustertexteLoaded = createAction(
	'[MustertexteFacade]: loadMustertexte',
	props<{mustertexte: Mustertext[]}>()
);

export const resetMustertexte = createAction(
	'[MustertexteFacade] resetState'
);
