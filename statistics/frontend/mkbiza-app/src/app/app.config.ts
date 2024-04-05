import { ApplicationConfig, LOCALE_ID, enableProdMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { appRoutes } from './app.routes';
import { environment } from 'src/environments/environment.prod';
import { registerLocaleData } from '@angular/common';
import { Configuration } from '@mkbiza-app/config';
import { MAT_DATE_LOCALE } from '@angular/material/core';

if (environment.production) {
  enableProdMode();
}

registerLocaleData(LOCALE_ID, 'de');

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(appRoutes),

    {
      provide: Configuration,
      useFactory: () =>
        new Configuration(
          environment.baseUrl,
          environment.assetsPath,
          'mkbiza-app',
          environment.production
        ),
    },
    {
      provide: MAT_DATE_LOCALE,
      useValue: 'de-DE',
    },
    { provide: LOCALE_ID, useValue: 'de-DE' },

  ],
};
