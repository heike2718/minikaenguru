/* eslint-disable */
module.exports = {
  name: 'admin-schulkatalog',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/admin-schulkatalog',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
