import { RegistrationMode } from '../domain/entities';
import { createReducer, Action, on } from '@ngrx/store';
import * as RegistrationActions from './registration.actions';


export const registrationFeatureKey = 'mkv-app-registration';

export interface RegistrationState {
	readonly mode: RegistrationMode;
	readonly schulkuerzel: string;
	readonly showSchulkatalog: boolean;
	readonly submitEnabled: boolean;
	readonly showRegistrationSuccessDialog: boolean;
	readonly registrationSuccessMessage: string;
}

export const initialRegistrationState: RegistrationState = {
	mode: undefined,
	schulkuerzel: undefined,
	showSchulkatalog: false,
	submitEnabled: false,
	showRegistrationSuccessDialog: false,
	registrationSuccessMessage: undefined
}

const registrationReducer = createReducer(initialRegistrationState,

	on(RegistrationActions.registrationModeChanged, (state, action) => {

		let showSchulkatalog = false;
		if (action.mode && action.mode === 'LEHRER' && !state.schulkuerzel) {
			showSchulkatalog = true;
		}

		return { mode: action.mode, schulkuerzel: state.schulkuerzel, showSchulkatalog: showSchulkatalog, submitEnabled: false }
	}),

	on(RegistrationActions.schuleSelected, (state, action) => {

		const enable = action.schulkuerzel ? true : false;
		return { ...state, schulkuerzel: action.schulkuerzel, submitEnabled: enable }
	}),

	on(RegistrationActions.resetRegistrationState, (_state, _action) => {

		return initialRegistrationState;
	}),

	on (RegistrationActions.userCreated, (_state, action) => {
		return { ...initialRegistrationState, showRegistrationSuccessDialog: true, registrationSuccessMessage: action.message }
	})
);

export function reducer(state: RegistrationState | undefined, action: Action) {
	return registrationReducer(state, action);
}


