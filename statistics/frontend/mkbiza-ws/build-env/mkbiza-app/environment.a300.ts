import { provideStoreDevtools } from '@ngrx/store-devtools';

export const environment = {
  production: false,
  baseUrl: '',
  withCredentials: true,
  assetsPath: '/mkbiza-app/assets/',
  providers: [
    provideStoreDevtools({ maxAge: 25 , connectInZone: true})
  ],
};