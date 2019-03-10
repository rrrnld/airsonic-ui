module.exports = function (config) {
  const configuration = {
    browsers: ['ChromeHeadless'],
    // The tests are sometimes run before the tests were completely written
    // to disc; this is a known problem unfortunately. This is a hack to at
    // least keep the browsers connected so the tests are compiled and run
    // again even if a developer isn't aware of this
    autoWatchBatchDelay: 100,
    browserNoActivityTimeout: 60 * 1000 * 10,
    // The directory where the output file lives
    basePath: 'public/test',
    // The file itself
    files: ['ci.js'],
    frameworks: ['cljs-test'],
    plugins: [
      'karma-cljs-test',
      'karma-chrome-launcher',
      'karma-notify-reporter' // reporters are set in package.json
    ],
    colors: true,
    logLevel: config.LOG_INFO,
    client: {
      args: ["shadow.test.karma.init"]
    },
    // configure travis-ci; based on this: https://stackoverflow.com/questions/19255976/how-to-make-travis-execute-angular-tests-on-chrome-please-set-env-variable-chr#25661593
    customLaunchers: {
      ChromeHeadlessTravisCI: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox']
      }
    }
  }

  if (process.env.TRAVIS) {
    configuration.browsers = ['ChromeHeadlessTravisCI']
  }


  config.set(configuration)
}
