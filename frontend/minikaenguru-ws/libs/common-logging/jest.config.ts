/* eslint-disable */
module.exports = {
  name: 'common-logging',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-logging',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};