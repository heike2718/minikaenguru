module.exports = {
  name: 'mkv-app',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/apps/mkv-app',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js'
  ]
};
