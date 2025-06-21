import { InjectionToken } from '@angular/core';


export interface MkAuthConfig {
	readonly baseUrl: string;
	readonly storagePrefix: string;
	readonly production: boolean;
	readonly loginSuccessUrl: string;
	readonly profileUrl: string;
}

export const MkAuthConfigService = new InjectionToken<MkAuthConfig>('MkAuthConfig');
