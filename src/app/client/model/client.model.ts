export const STORAGE_KEY_CLIENT = 'mkadmin_client';

export interface Client {
	accessToken: string;
	expiresAt: number;   // client_token_expires_at ist in Millisekunden seit 01.01.1970
}

export interface RefreshJWTPayload {
	clientAccessToken: string[];
	userRefreshToken: string;
	force: boolean;
}

export interface JWTPayload {
	jwt: string;
	expiresAtSeconds: number;
}

