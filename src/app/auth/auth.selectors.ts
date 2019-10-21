import { createSelector, createFeatureSelector } from '@ngrx/store';
import * as fromAuth from './auth.reducer';

export const selectAuthState = createFeatureSelector<fromAuth.AuthState>(fromAuth.authFeatureKey);

// memorized function: value is going to be calculated only if the selected part of state has changed
export const isLoggedIn = createSelector(
	selectAuthState,
	auth => !!auth.user
);

// selectors can be combined
export const isLoggedOut = createSelector(
	isLoggedIn,
	loggedIn => !loggedIn
);

