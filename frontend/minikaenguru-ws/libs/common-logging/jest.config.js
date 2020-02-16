module.exports = {
  name: 'common-logging',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/common-logging',
  snapshotSerializers: [
    'jest-preset-angular/AngularSnapshotSerializer.js',
    'jest-preset-angular/HTMLCommentSerializer.js'
  ]
};
