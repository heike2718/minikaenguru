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

