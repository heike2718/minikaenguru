import { createAction, props } from '@ngrx/store';

export const startLoading = createAction(
	'[UrkundenFacade] diverse before backend call'
);

export const downloadFinished = createAction(
	'[UrkundenFacade] downloadUrkunde'
);

export const resetModule = createAction(
	'[UrkundenFacade] resetState'
);

