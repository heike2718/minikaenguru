export const STORAGE_KEY_ID_REFERENCE = 'id_reference';
export const STORAGE_KEY_SESSION_EXPIRES_AT = 'session_expires_at';
export const STORAGE_KEY_DEV_SESSION_ID = 'dev_session_id';
export const STORAGE_KEY_USER = 'user';

export type Rolle = 'LEHRER' | 'PRIVAT' | 'ADMIN';

export interface AuthResult {
	expiresAt?: number;
	state?: string;
	nonce?: string;
	idToken: string;
}

export interface User {
	readonly idReference: string;
	readonly rolle: string;
	readonly fullName: string;
}

export interface Session {
	readonly sessionId?: string;
	readonly expiresAt: number;
	readonly user?: User;
}
