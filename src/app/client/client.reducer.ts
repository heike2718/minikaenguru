
import { createReducer, on } from '@ngrx/store';
import { Client } from './model/client.model';
import { ClientActions } from './client-action-types';

export const clientFeatureKey = 'client';

export interface ClientState {
	client: Client;
}

export const initialClientState: ClientState = {
	client: undefined
};

export const clientReducer = createReducer(
	initialClientState,
	on(ClientActions.initAccessToken, (_state, action) => {
		return {
			client: action.client
		};
	}),
	on(ClientActions.refereshAccessToken, (_state, action) => {
		return {
			client: action.client
		};
	})
);

