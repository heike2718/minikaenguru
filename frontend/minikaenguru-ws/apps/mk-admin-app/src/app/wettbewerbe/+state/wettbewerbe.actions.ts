import { createAction, props } from '@ngrx/store';
import { Wettbewerb, WettbewerbStatus } from '../wettbewerbe.model';
import { Message } from '@minikaenguru-ws/common-messages';

export const loadWettbewerbe = createAction(
	'[WettbewerbeListResolver]'
);

export const allWettbewerbeLoaded = createAction(
	'[WettbewerbeEffects] loadWettbewerbe$',
	props<{ wettbewerbe: Wettbewerb[] }>()
);

export const loadWettbewerbDetails = createAction(
	'[WettbewerbDashboardResolver]',
	props<{ jahr: number }>()
);

export const createNewWettbewerb = createAction(
	'[WettbewerbFacade] createNewWettbewerb'
);

export const startEditingWettbewerb = createAction(
	'[WettbewerbFacade] editWettbewerb',
	props<{ wettbewerb: Wettbewerb }>()
);

export const wettbewerbInserted = createAction(
	'[WettbewerbFacade]: insertWettbewerb',
	props<{ wettbewerb: Wettbewerb, outcome: Message }>()
);

export const wettbewerbMovedOn = createAction(
	'[WettbewerbFacade]: moveWettbewerbOn',
	props<{ wettbewerb: Wettbewerb, neuerStatus: WettbewerbStatus, outcome: Message }>()
);

export const wettbewerbUpdated = createAction(
	'[WettbewerbFacade]: updateWettbewerb',
	props<{ wettbewerb: Wettbewerb, outcome: Message }>()
);

export const saveFailed = createAction(
	'[WettbewerbFacade]: insert/move on/update',
	props<{ outcome: Message }>()
);

export const editWettbewerbFinished = createAction(
	'[WettbewerbEditorComponent] close'
);

export const selectWettbewerbsjahr = createAction(
	'[WettbewerbCardComponent] click',
	props<{ jahr: number }>()
);

export const selectedWettbewerbLoaded = createAction(
	'[WettbewerbeEffects] loadWettbewerbDetails$',
	props<{ wettbewerb: Wettbewerb }>()
);

export const resetWettbewerbe = createAction(
	'[NavbarComponent] login'
);
