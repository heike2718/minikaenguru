
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
			jwt: state.jwt ? Object.assign({}, state.jwt) : undefined
		};
	}),
	on(ClientActions.refereshAccessToken, (state, action) => {
		return {
			client: action.client,
			jwt: state.jwt ? Object.assign({}, state.jwt) : undefined
		};
	}),
	on(ClientActions.initJWT, (state, action) => {
		return {
			client: state.client ? Object.assign({}, state.client) : undefined,
			jwt: action.jwt
		};
	}),
	on(ClientActions.refereshJWT, (state, action) => {
		return {
			client: state.client ? Object.assign({}, state.client) : undefined,
			jwt: action.jwt
		};
	}),
	on(ClientActions.logout, (state, _action) => {
		return {
			client: state.client ? Object.assign({}, state.client) : undefined,
			jwt: undefined
		};
	}),
	on(ClientActions.deleteJWT, (state, _action) => {
		return {
			client: state.client ? Object.assign({}, state.client) : undefined,
			jwt: undefined
		};
	})
);

