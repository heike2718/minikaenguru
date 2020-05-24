import {createAction, props} from '@ngrx/store';
import { Wettbewerb } from '../wettbewerbe.model';

export const loadWettbewerbe = createAction(
	'[WettbewerbeListResolver]'
);

export const allWettbewerbeLoaded = createAction(
	'[WettbewerbeEffects] loadWettbewerbe$',
	props<{wettbewerbe: Wettbewerb[]}>()
);

export const loadWettbewerbDetails = createAction(
	'[WettbewerbDashboardResolver]',
	props<{jahr: number}>()
);

export const selectWettbewerbsjahr = createAction(
	'[WettbewerbCardComponent] click',
	props<{jahr: number}>()
);

export const selectedWettbewerbLoaded = createAction(
	'[WettbewerbeEffects] loadWettbewerbDetails$',
	props<{wettbewerb: Wettbewerb}>()
);
