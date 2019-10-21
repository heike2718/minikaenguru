import { createSelector, createFeatureSelector } from '@ngrx/store';
import * as fromClient from './client.reducer';
import { jwtExprired } from './client.utils';


export const selectClientState = createFeatureSelector<fromClient.ClientState>(fromClient.clientFeatureKey);

export const isLoggedOut = createSelector(
	selectClientState,
	state => {
		if (!state.jwt) {
			return true;
		}
		const expired = jwtExprired(state.jwt.expiresAtSeconds);
		console.log('[select client state] isLoggedOut=' + expired);
		return expired;
	}
);

export const isLoggedIn = createSelector(
	isLoggedOut,
	loggedOut => !loggedOut
);


