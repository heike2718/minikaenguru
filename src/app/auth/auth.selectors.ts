import { createSelector, createFeatureSelector } from '@ngrx/store';
import * as fromAuth from './auth.reducer';

export const selectAuthState = createFeatureSelector<fromAuth.AuthState>(fromAuth.authFeatureKey);

// memorized function: value is going to be calculated only if the selected part of state has changed
export const userLoaded = createSelector(
	selectAuthState,
	auth => !!auth.user
);

