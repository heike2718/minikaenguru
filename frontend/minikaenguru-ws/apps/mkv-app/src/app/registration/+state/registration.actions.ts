import { createAction, props } from '@ngrx/store';
import { RegistrationMode } from '../domain/entities';


export const registrationModeChanged = createAction(
	'[RegistrationComponent] registrationModeChanged',
	props<{mode: RegistrationMode}>()
);

export const schuleSelected = createAction(
	'[RegistrationComponent] schuleSelected',
	props<{schulkuerzel: string}>()
);

export const resetSchulsuche = createAction(
	'[RegistrationComponent] neueSchulsuche'
);

export const resetRegistrationState = createAction(
	'[RegistrationComponent] resetRegistrationState'
);

export const userCreated = createAction(
	'[RegistrationService] userCreated',
	props<{message: string}>()
);

