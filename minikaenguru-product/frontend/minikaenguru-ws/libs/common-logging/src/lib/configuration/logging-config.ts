import { InjectionToken } from '@angular/core';

export interface LoggingConfig {
	readonly consoleLogActive: boolean;
	readonly serverLogActive: boolean;
	readonly loglevel: number;
}

export const LoggingConfigService = new InjectionToken<LoggingConfig>('LoggingConfig');
