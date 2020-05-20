import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAuth from './auth.reducer';


export const selectAuthState =
    createFeatureSelector<fromAuth.AuthState>(fromAuth.authFeatureKey);

export const session = createSelector(selectAuthState, s => s.session);
export const user = createSelector(session, s => s.user);
export const isLoggedIn = createSelector(session, s =>  s.user ? true : false);
export const isLoggedOut = createSelector(isLoggedIn, loggedIn => !loggedIn);

