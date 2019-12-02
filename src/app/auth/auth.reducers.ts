import { createReducer, on, State, Action } from '@ngrx/store';
import { User } from './model/user.model';
import { AuthActions } from './auth-action-types';

export const authFeatureKey = 'auth';

export interface AuthState {
	user: User;
}

export const initialAuthState: AuthState = {
	user: undefined
};

// Macht Problem beim AOT build, siehe https://github.com/ngrx/platform/issues/1915
export const authReducer = createReducer(
	initialAuthState,
	on(AuthActions.loadUser, (_state, action) => {
		return {
			user: action.user
		};
	}),
	on(AuthActions.logOut, (_state, _action) => {
		return {
			user: undefined
		};
	})
);

// see https://github.com/ngrx/platform/issues/1915
// and Udemy course comment https://www.udemy.com/course/ngrx-course/learn/lecture/15942130#questions/8441474
export function authReducerWrapper(state, action) {
	return authReducer(state, action);
}

