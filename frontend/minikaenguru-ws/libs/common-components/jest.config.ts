/* eslint-disable */
module.exports = {
  name: 'common-components',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-components',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
