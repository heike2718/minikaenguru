import { ApplicationConfig, ErrorHandler, LOCALE_ID, enableProdMode, importProvidersFrom } from '@angular/core';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { appRoutes } from './app.routes';
import { environment } from 'src/environments/environment';
import { registerLocaleData } from '@angular/common';
import { Configuration } from '@mkbiza-app/config';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { LoadingInterceptor } from '@mkbiza-app/messages-api';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ErrorInterceptor, MkbizaAPIHttpInterceptor } from '@mkbiza-app/http';
import { ErrorHandlerService } from './error/error-handler.service';
import { provideStore } from '@ngrx/store';
import { domainDataProvider } from '@mkbiza-app/domain-api';

if (environment.production) {
  enableProdMode();
}

registerLocaleData(LOCALE_ID, 'de');

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(appRoutes),
    provideAnimations(),
    /** das muss so gemacht werden, weil ohne den Parameter {} nichts da ist, wohinein man den state hängen könnte */
    provideStore(
      {}
    ),
    environment.providers,
    domainDataProvider,
    importProvidersFrom(
      HttpClientModule
    ),
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

