import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAuth from './auth.reducer';


export const selectAuthState =
    createFeatureSelector<fromAuth.AuthState>(fromAuth.authFeatureKey);

export const selectSession = createSelector(selectAuthState, s => s.session);
export const selectUser = createSelector(selectSession, s => s.user);
export const selectIsLoggedIn = createSelector(selectSession, auth =>  auth.user);
export const selectIsLoggedOut = createSelector(selectSession, auth =>  !auth.user);

