/* eslint-disable */
module.exports = {
  name: 'common-schulkatalog',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-schulkatalog',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
