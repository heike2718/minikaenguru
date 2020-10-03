import { createAction, props } from '@ngrx/store';
import { Veranstalter } from '../veranstalter.model';

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



