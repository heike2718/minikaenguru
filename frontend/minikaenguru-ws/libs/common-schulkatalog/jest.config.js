module.exports = {
  name: 'common-schulkatalog',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-schulkatalog',
  snapshotSerializers: [
    'jest-preset-angular/AngularSnapshotSerializer.js',
    'jest-preset-angular/HTMLCommentSerializer.js'
  ]
};
