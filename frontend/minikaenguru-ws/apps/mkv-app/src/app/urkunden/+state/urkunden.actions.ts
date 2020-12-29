import { createAction, props } from '@ngrx/store';
import { UrkundenauftragEinzelkind } from '../urkunden.model';

export const initialerUrkundenauftragCreated = createAction(
	'[UrkundenFacade] createUrkundenauftrag',
	props<{auftrag: UrkundenauftragEinzelkind}>()
);

export const startLoading = createAction(
	'[UrkundenFacade] diverse before backend call'
);

export const downloadFinished = createAction(
	'[UrkundenFacade] downloadUrkunde'
);

export const finishedWithError = createAction(
	'[UrkundenFacade] diverse on error'
);

export const urkundenauftragVersendet = createAction(
	'[UrkundenFacade] generiereEinzelurkunde'
);

export const resetModule = createAction(
	'[UrkundenFacade] resetState'
);

