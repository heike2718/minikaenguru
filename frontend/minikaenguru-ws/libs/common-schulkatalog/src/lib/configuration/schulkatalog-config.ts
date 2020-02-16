import { InjectionToken } from '@angular/core';

export interface SchulkatalogConfig {
    readonly baseUrl: string;
}

export const SchulkatalogConfigService = new InjectionToken<SchulkatalogConfig>('SchulkatalogConfig');

