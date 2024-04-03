import { ApplicationConfig, LOCALE_ID, enableProdMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { appRoutes } from './app.routes';
import { environment } from 'src/environments/environment.prod';
import { registerLocaleData } from '@angular/common';

if (environment.production) {
  enableProdMode();
}

registerLocaleData(LOCALE_ID, 'de');

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(appRoutes)
  ],
};
