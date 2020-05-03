import { Action, createReducer, on } from '@ngrx/store';
import { Session } from '../domain/entities';
import * as AuthActions from './auth.actions';


export const authFeatureKey = 'auth';

export interface AuthState {
	session: Session
};

export const initialAuthState: AuthState = {
	session: {
		sessionId: undefined,
		expiresAt: 0,
		user: undefined
	}
};


const authReducer = createReducer(initialAuthState,

	on(AuthActions.login, (state, action) => {
		console.log('logged in');
		return { ...state, session: action.session }
	}),

	on(AuthActions.logout, (_state, _action) => {
		return initialAuthState
	}),

	on(AuthActions.refreshSession, (state, action) => {
		return { ...state, session: action.session }
	}),

);

export function reducer(state: AuthState | undefined, action: Action) {
	return authReducer(state, action);
}

