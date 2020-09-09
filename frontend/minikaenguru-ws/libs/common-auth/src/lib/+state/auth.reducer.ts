import { Action, createReducer, on } from '@ngrx/store';
import { Session } from '../domain/entities';
import * as AuthActions from './auth.actions';


export const authFeatureKey = 'auth';

export interface AuthState {
	session: Session,
	loggingOut: boolean
};

export const initialAuthState: AuthState = {
	session: {
		sessionId: undefined,
		expiresAt: 0,
		user: undefined
	},
	loggingOut: false
};


const authReducer = createReducer(initialAuthState,

	on(AuthActions.login, (state, action) => {
		return { ...state, session: action.session, loggingOut: false };
	}),

	on(AuthActions.logout, (_state, _action) => {
		return initialAuthState;
	}),

	on(AuthActions.refreshSession, (state, action) => {
		return { ...state, session: action.session };
	}),

	on(AuthActions.startLoggingOut, (state,_action) => {
		return {...state, loggingOut: true};
	})

);

export function reducer(state: AuthState | undefined, action: Action) {
	return authReducer(state, action);
}

