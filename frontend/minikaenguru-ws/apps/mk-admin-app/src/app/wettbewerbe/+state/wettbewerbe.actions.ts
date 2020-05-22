import {createAction, props} from '@ngrx/store';
import { Wettbewerb } from '../wettbewerbe.model';

export const loadWettbewerbe = createAction(
	'[Wettbewerbe Resolver] Load all wettbewerbe'
);

export const allWettbewerbeLoaded = createAction(
	'[WettbewerbeService] loadWettbewerbe',
	props<{wettbewerbe: Wettbewerb[]}>()
);
