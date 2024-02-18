import { createAction, props } from '@ngrx/store';
import { Versandauftrag } from '../../shared/newsletter-versandauftrage.model';


export const startBackendCall = createAction(
	'[VersandauftraegeFacade] diverse'
);

export const backendCallFinishedWithError = createAction(
	'[VersandauftraegeFacade] errors'
);

export const versandauftraegeLoaded = createAction(
	'[VersandauftraegeFacade]: loadVersandaufraege',
	props<{versandauftraege: Versandauftrag[]}>()
);


export const versandauftragSelected = createAction(
	'[VersandauftraegeFacade]: selectVersandauftrag',
	props<{versandauftrag: Versandauftrag}>()
);

export const versandauftragUnselected = createAction(
	'[VersandauftraegeFacade]: unselectVersandauftrag'
);

export const resetVersandauftraege = createAction(
	'[VersandauftraegeFacade]: resetState'
);

