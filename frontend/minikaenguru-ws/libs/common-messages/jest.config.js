module.exports = {
  name: 'common-messages',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-messages',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js'
  ]
};
