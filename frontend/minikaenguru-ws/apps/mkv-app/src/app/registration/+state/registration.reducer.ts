import { RegistrationMode } from '../domain/entities';
import { createReducer, Action } from '@ngrx/store';


export const registrationFeatureKey = 'mkv-app-registration';

export interface RegistrationState {
	readonly mode: RegistrationMode;
	readonly schulkuerzel: string;
	readonly submitEnabled: boolean;
}

export const initialRegistrationState: RegistrationState = {
	mode: undefined,
	schulkuerzel: undefined,
	submitEnabled: false
}

const registrationReducer = createReducer(initialRegistrationState,

);

export function reducer(state: RegistrationState | undefined, action: Action) {
	return registrationReducer(state, action);
}


