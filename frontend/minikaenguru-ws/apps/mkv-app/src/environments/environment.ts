// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  envName: 'DEV',
  production: false,
  assetsUrl: '/home/heike/git/minikaenguru/frontend/minikaenguru-ws/apps/mkv-app/src/assets',
  apiUrl: 'http://192.168.10.176:9510/mk-gateway',
  profileUrl: 'http://192.168.10.176:80/profil-app',
  storageKeyPrefix: 'mkv_',
  version: '0.2.8',
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
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
