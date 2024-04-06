import { ApplicationConfig, ErrorHandler, LOCALE_ID, enableProdMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { appRoutes } from './app.routes';
import { environment } from 'src/environments/environment.prod';
import { registerLocaleData } from '@angular/common';
import { Configuration } from '@mkbiza-app/config';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { LoadingInterceptor } from '@mkbiza-app/messages-api';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ErrorInterceptor, MkbizaAPIHttpInterceptor } from '@mkbiza-app/http';
import { ErrorHandlerService } from './error/error-handler.service';

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
    { provide: ErrorHandler, useClass: ErrorHandlerService },
    { provide: HTTP_INTERCEPTORS, multi: true, useClass: LoadingInterceptor },
    {
      provide: HTTP_INTERCEPTORS,
      multi: true,
      useClass: MkbizaAPIHttpInterceptor,
    },
    { provide: HTTP_INTERCEPTORS, multi: true, useClass: ErrorInterceptor }
  ],
};
