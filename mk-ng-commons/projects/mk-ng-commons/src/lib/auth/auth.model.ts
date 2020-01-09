

export interface User {
	rolle: string;
}

export interface Session {
	sessionId?: string; // only required in dev since cookies do not work in dev
	user?: User; // indicates a logged in user if present
}

export class StorageKey {
	constructor(public prefix: string, public name: string) { }


	public getValue() {
		return this.prefix + '_' + this.name;
	}
}

export type Roles = 'ADMIN' | 'LEHRER' | 'PRIVAT';

