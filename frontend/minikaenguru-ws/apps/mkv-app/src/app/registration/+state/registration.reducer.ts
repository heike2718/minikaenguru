import { RegistrationMode } from '../domain/entities';
import { createReducer, Action, on } from '@ngrx/store';
import * as RegistrationActions from './registration.actions';


export const registrationFeatureKey = 'mkv-app-registration';

export interface RegistrationState {
	readonly mode: RegistrationMode;
	readonly schulkuerzel: string;
	readonly showSchulkatalog: boolean;
	readonly submitEnabled: boolean;
}

export const initialRegistrationState: RegistrationState = {
	mode: undefined,
	schulkuerzel: undefined,
	showSchulkatalog: false,
	submitEnabled: false
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

	on(RegistrationActions.resetRegistrationState, (state, action) => {

		return initialRegistrationState;
	}),
);

export function reducer(state: RegistrationState | undefined, action: Action) {
	return registrationReducer(state, action);
}


