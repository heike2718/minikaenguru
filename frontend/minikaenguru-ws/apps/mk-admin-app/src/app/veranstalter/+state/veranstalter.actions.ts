import { createAction, props } from '@ngrx/store';
import { Veranstalter, ZugangUnterlagen } from '../veranstalter.model';
import { PrivatteilnahmeAdminOverview } from '../teilnahmen.model';

export const startSuche = createAction(
	'[VeranstalterFacade] sucheVeranstalter load'
);

export const sucheFinished = createAction(
	'[VeranstalterFacade] sucheVeranstalter success',
	props<{veranstalter: Veranstalter[]}>()
);

export const sucheFinishedWithError = createAction(
	'[VeranstalterFacade] sucheVeranstalter failure'
);

export const veranstalterSelected = createAction(
	'[VeranstalterFacade] selectVeranstalter',
	props<{veranstalter: Veranstalter}>()
);

export const resetVeranstalters = createAction(
	'[NavbarComponent] - veranstalter login/logout'
);

export const startLoadDetails = createAction(
	'[VeranstalterFacade] findOrLoad beforeLoad'
);

export const loadDetailsFinishedWithError = createAction(
	'[VeranstalterFacade] findOrLoad error'
);


export const privatteilnahmeOverviewLoaded = createAction(
	'[VeranstalterFacade] findOrLoadPrivatteilnahmeAdminOverview',
	props<{privatteilnahmeOverview: PrivatteilnahmeAdminOverview}>()
);

export const zugangsstatusUnterlagenGeaendert = createAction(
	'[VeranstalterFacade] zugangsstatusUnterlagenAendern',
	props<{veranstalter: Veranstalter, neuerStatus: ZugangUnterlagen}>()
);



