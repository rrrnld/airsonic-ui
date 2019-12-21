const { execSync } = require('child_process')

module.exports = function (config) {

  let browsers = null;
  if (process.env.TRAVIS || process.env.CI) {
    // custom config for continuous integration
    browsers = ['ChromeHeadlessCI']
  } else {
    try {
      // if Firefox is installed, use that as the test runner
      execSync('which firefox')
      browsers = ['FirefoxHeadless']
    } catch (_) {
      browsers = ['ChromeHeadless']
    }
  }

  const configuration = {
    browsers: browsers,
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
      'karma-firefox-launcher',
      'karma-notify-reporter' // reporters are set in package.json
    ],
    colors: true,
    logLevel: config.LOG_INFO,
    client: {
      args: ["shadow.test.karma.init"]
    },
    // configure travis-ci; based on this: https://stackoverflow.com/questions/19255976/how-to-make-travis-execute-angular-tests-on-chrome-please-set-env-variable-chr#25661593
    customLaunchers: {
      ChromeHeadlessCI: {
        base: 'ChromeHeadless',
        flags: ['--no-sandbox', '--headless', '--nogpu']
      }
    }
  }

  config.set(configuration)
}
