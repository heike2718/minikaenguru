import { InjectionToken } from '@angular/core';

export interface MkComponentsConfig {
	readonly baseUrl: string;
}


export const MkComponentsConfigService = new InjectionToken<MkComponentsConfig>('MkComponentsConfig');
