import { createSelector, createFeatureSelector } from '@ngrx/store';
import * as fromAuth from './auth.reducers';

export const selectAuthState = createFeatureSelector<fromAuth.AuthState>(fromAuth.authFeatureKey);

// memorized function: value is going to be calculated only if the selected part of state has changed
export const isLoggedOut = createSelector(
	selectAuthState,
	state => !state.user
);

export const isLoggedIn = createSelector(
	isLoggedOut,
	loggedOut => !loggedOut
);

