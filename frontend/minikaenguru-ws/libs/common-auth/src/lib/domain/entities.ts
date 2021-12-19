export const STORAGE_KEY_ID_REFERENCE = 'id_reference';
export const STORAGE_KEY_SESSION_EXPIRES_AT = 'session_expires_at';
export const STORAGE_KEY_DEV_SESSION_ID = 'dev_session_id';
export const STORAGE_KEY_USER = 'user';
export const STORAGE_KEY_INVALID_SESSION = 'sessionInvalidated';

export type Rolle = 'LEHRER' | 'PRIVAT' | 'ADMIN';

export interface AuthResult {
	expiresAt?: number;
	state?: string;
	nonce?: string;
	idToken?: string;
}

export interface User {
	readonly idReference: string;
	readonly rolle: Rolle;
	readonly fullName: string;
}

export interface Session {
	readonly sessionId: string | null;
	readonly expiresAt: number;
	readonly user: User | null;
}

