// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  envName: 'DEV',
  production: false,
  assetsUrl: '/home/heike/git/minikaenguru/frontend/minikaenguru-ws/apps/mkv-app/src/assets',
  apiUrl: 'http://localhost:9510/mk-gateway',
  profileUrl: 'http://localhost:80/profil-app',
  storageKeyPrefix: 'mkv_',
  version: '2.3.1',
  consoleLogActive: true,
  serverLogActive: false,
  loglevel: 1
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
