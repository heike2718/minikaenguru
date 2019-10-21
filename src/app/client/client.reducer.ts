
import { createReducer, on } from '@ngrx/store';
import { Client, JWTPayload } from './model/client.model';
import { ClientActions } from './client-action-types';

export const clientFeatureKey = 'client';

export interface ClientState {
	client: Client;
	jwt: JWTPayload;
}

export const initialClientState: ClientState = {
	client: undefined,
	jwt: undefined
};

export const clientReducer = createReducer(
	initialClientState,
	on(ClientActions.initAccessToken, (state, action) => {
		return {
			client: action.client,
			jwt: Object.assign({}, state.jwt)
		};
	}),
	on(ClientActions.refereshAccessToken, (state, action) => {
		return {
			client: action.client,
			jwt: Object.assign({}, state.jwt)
		};
	}),
	on(ClientActions.initJWT, (state, action) => {
		return {
			client: Object.assign({}, state.client),
			jwt: action.jwt
		};
	}),
	on(ClientActions.refereshJWT, (state, action) => {
		return {
			client: Object.assign({}, state.client),
			jwt: action.jwt
		};
	}),
	on(ClientActions.logout, (state, _action) => {
		return {
			client: Object.assign({}, state.client),
			jwt: undefined
		};
	})
);

