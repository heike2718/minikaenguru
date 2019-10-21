export const SUFFIX_KEY_CLIENT_ACCESS_TOKEN = 'client_access_token';
export const STORAGE_KEY_CLIENT_EXPIRES_AT = 'mkadmin_client_token_expires_at';
export const STORAGE_KEY_CLIENT_ACCESS_TOKEN = 'mkadmin_client_access_token';

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

