import { createAction, props } from '@ngrx/store';
import { RegistrationMode } from '../domain/entities';


export const registrationModeChanged = createAction(
	'[RegistrationComponent] registrationModeChanged',
	props<{mode: RegistrationMode}>()
);

export const newsletterAbonierenChanged = createAction(
	'[RegistrationComponent] newsletterAboChanged',
	props<{flag: boolean}>()
)

export const schuleSelected = createAction(
	'[RegistrationComponent] schuleSelected',
	props<{schulkuerzel: string}>()
);

export const resetSchulsuche = createAction(
	'[RegistrationComponent] neueSchulsuche'
);

export const resetRegistrationState = createAction(
	'[RegistrationComponent] initState'
);

export const userCreated = createAction(
	'[RegistrationService] userCreated',
	props<{message: string}>()
);



