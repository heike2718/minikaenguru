/* eslint-disable */
module.exports = {
  name: 'mkod-app',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/apps/mkod-app',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
