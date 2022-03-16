import { createAction, props } from '@ngrx/store';
import { Mustertext, MUSTRETEXT_KATEGORIE } from '../../shared/shared-entities.model';


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

export const filterkriteriumChanged = createAction(
	'[MustertexteFacade] selectFilter',
	props<{neuerFilter: MUSTRETEXT_KATEGORIE}>()
)

export const mustertextDeleted = createAction(
	'[MustertexteFacade] deleteMustertext',
	props<{mustertext: Mustertext}>()
);

export const resetMustertexte = createAction(
	'[MustertexteFacade] resetState'
);
