import { InjectionToken } from '@angular/core';


export interface MkvAuthConfig {
	readonly baseUrl: string;
}

export const MkvAuthConfigService = new InjectionToken<MkvAuthConfig>('MkvAuthConfig');
