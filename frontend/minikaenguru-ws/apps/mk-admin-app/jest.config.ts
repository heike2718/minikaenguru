/* eslint-disable */
module.exports = {
  name: 'mk-admin-app',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/apps/mk-admin-app',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
