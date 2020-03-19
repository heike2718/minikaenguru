import { InjectionToken } from '@angular/core';

export interface SchulkatalogConfig {
    readonly baseUrl: string;
    readonly devmode: boolean;
    readonly admin: boolean;
}

export const SchulkatalogConfigService = new InjectionToken<SchulkatalogConfig>('SchulkatalogConfig');

