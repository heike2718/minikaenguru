import { createSelector, createFeatureSelector } from '@ngrx/store';
import * as fromClient from './client.reducer';
import * as moment from 'moment';


export const selectClientState = createFeatureSelector<fromClient.ClientState>(fromClient.clientFeatureKey);

export const isClientTokenValid = createSelector(
	selectClientState,
	state => {
		if (!state.client) {
			return false;
		}
		const expiration = moment(state.client.expiresAt);
		// lassen 3,5 Minuten Vorsprung zum refreshen des accessTokens.
		const expired = moment().add(210, 'seconds').isAfter(expiration);
		return expired;
	}
);

