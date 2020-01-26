module.exports = {
  name: 'schulkatalog-api',
  preset: '../../../jest.config.js',
  coverageDirectory: '../../../coverage/libs/schulkatalog/api',
  snapshotSerializers: [
    'jest-preset-angular/AngularSnapshotSerializer.js',
    'jest-preset-angular/HTMLCommentSerializer.js'
  ]
};
