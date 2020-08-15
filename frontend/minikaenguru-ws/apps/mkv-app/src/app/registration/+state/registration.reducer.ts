import { RegistrationMode } from '../domain/entities';
import { createReducer, Action, on } from '@ngrx/store';
import * as RegistrationActions from './registration.actions';


export const registrationFeatureKey = 'mkv-app-registration';

export interface RegistrationState {
	readonly mode: RegistrationMode;
	readonly newsletterAbonieren: boolean;
	readonly schulkuerzel: string;
	readonly showSchulkatalog: boolean;
	readonly submitEnabled: boolean;
	readonly showRegistrationSuccessDialog: boolean;
	readonly registrationSuccessMessage: string;
}

export const initialRegistrationState: RegistrationState = {
	mode: undefined,
	newsletterAbonieren: false,
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

		return { mode: action.mode, schulkuerzel: state.schulkuerzel, showSchulkatalog: showSchulkatalog, submitEnabled: false, newsletterAbonieren: state.newsletterAbonieren }
	}),

	on(RegistrationActions.newsletterAbonierenChanged, (state, action) => {
		return {...state, newsletterAbonieren: action.flag};
	}),

	on(RegistrationActions.schuleSelected, (state, action) => {

		const enable = action.schulkuerzel ? true : false;
		return { ...state, schulkuerzel: action.schulkuerzel, submitEnabled: enable, showSchulkatalog: false }
	}),

	on(RegistrationActions.resetSchulsuche, (state, _action) => {

		return {...state, schulkuerzel: undefined, submitEnabled: false, showSchulkatalog: true};
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


