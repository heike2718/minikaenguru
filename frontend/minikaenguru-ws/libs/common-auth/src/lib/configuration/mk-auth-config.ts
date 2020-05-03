import { InjectionToken } from '@angular/core';


export interface MkAuthConfig {
	readonly baseUrl: string;
	readonly storagePrefix: string;
}

export const MkAuthConfigService = new InjectionToken<MkAuthConfig>('MkAuthConfig');
