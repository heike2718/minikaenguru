import { InjectionToken } from '@angular/core';

export interface AdminSchulkatalogConfig {
    readonly baseUrl: string;
    readonly devmode: boolean;
}

export const AdminSchulkatalogConfigService = new InjectionToken<AdminSchulkatalogConfig>('AdminSchulkatalogConfig');

