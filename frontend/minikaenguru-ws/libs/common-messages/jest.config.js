module.exports = {
  name: 'common-messages',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-messages',
  snapshotSerializers: [
    'jest-preset-angular/AngularSnapshotSerializer.js',
    'jest-preset-angular/HTMLCommentSerializer.js'
  ]
};
