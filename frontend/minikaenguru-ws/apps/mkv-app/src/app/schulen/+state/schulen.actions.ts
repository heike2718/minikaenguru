import { createAction, props } from '@ngrx/store';
import { Schule } from '../schulen.model';

export const loadSchulen = createAction(
	'[SchulenResolver] load schulen'
);

export const schulenLoaded = createAction(
    '[SchulenFacade] loadSchulen',
    props<{schulen: Schule[]}>()
);

export const selectSchule = createAction(
	'[SchuleCardComponent] select schule',
	props<{schule: Schule}>()
);

export const unselectSchule = createAction(
	'[SchuleCardComponent] unselect schule'
);

export const resetSchulen = createAction(
	'[SchulenFacade] resetState()'
);

