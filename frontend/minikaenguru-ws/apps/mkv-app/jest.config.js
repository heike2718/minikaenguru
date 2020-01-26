module.exports = {
  name: 'mkv-app',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/apps/mkv-app',
  snapshotSerializers: [
    'jest-preset-angular/AngularSnapshotSerializer.js',
    'jest-preset-angular/HTMLCommentSerializer.js'
  ]
};
