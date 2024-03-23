import { createAction, props } from '@ngrx/store';
import { Wettbewerb } from '../wettbewerb.model';

export const startLoadWettbewerbe = createAction(
	'[WettbewerbeFacade] loadWettbewerbe'
);

export const wettbewerbeLoaded = createAction(
    '[WettbewerbeFacade] loadWettbewerbe success',
    props<{wettbewerbe: Wettbewerb[]}>()
);

export const loadFinishedWithError = createAction(
	'[WettbewerbeFacade] loadWettbewerbe error'
);

export const wettbewerbSelected = createAction(
    '[WettbewerbFacade] selectWettbewerb',
    props<{wettbewerb: Wettbewerb}>()
);

export const wettbewerbDeselected = createAction(
    '[WettbewerbFacade] clearWettbewerbSelection'
);

export const reset = createAction(
	'[WettbewerbeFacade] reset'
);
