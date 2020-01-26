module.exports = {
  name: 'schulkatalog-domain',
  preset: '../../../jest.config.js',
  coverageDirectory: '../../../coverage/libs/schulkatalog/domain',
  snapshotSerializers: [
    'jest-preset-angular/AngularSnapshotSerializer.js',
    'jest-preset-angular/HTMLCommentSerializer.js'
  ]
};
