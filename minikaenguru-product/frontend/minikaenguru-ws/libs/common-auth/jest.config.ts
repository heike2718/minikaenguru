/* eslint-disable */
module.exports = {
  name: 'common-auth',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-auth',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
